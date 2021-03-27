package com.sebastian.defensecommander;

import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ScoreDatabaseHandler implements Runnable {
    HashMap<Integer,Player> info;
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private final MainActivity mainActivity;
    private static String dbURL;
    private Connection conn;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
    private int play10Score = 0;
    private final String initial;
    private final int score;
    private final int level;
    private final AlertDialog.Builder builder;

    public ScoreDatabaseHandler(MainActivity mainActivity, String initial, int score, int level) {
        this.mainActivity = mainActivity;
        this.initial = initial;
        this.score = score;
        this.level = level;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
        builder = new AlertDialog.Builder(mainActivity);
    }

    @Override
    public void run() {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
            StringBuilder sb = new StringBuilder();
            if (initial != null){
                insertScore(initial, score, level);
            }
            sb.append(getTopTen());
            String s = sb.toString();
            if ( initial == null && info != null){
                play10Score = Objects.requireNonNull(info.get(10)).getScore();
            }
            if (score > play10Score && initial == null) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialogMessage(s);
                    }
                });

            } else {
                mainActivity.setResultsFromDatabase(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertScore(String initial, int score, int level) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "INSERT INTO AppScores VALUES (" +
                System.currentTimeMillis() + ", '" + initial + "', " + score + ", " +
                level +
                ")";
        int result = stmt.executeUpdate(sql);
        stmt.close();
    }

    private String getTopTen() throws SQLException {
        info = new HashMap<>();
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM AppScores ORDER BY Score DESC LIMIT 10";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(),
                "%2s %8s %8s %8s %16s%n", "#", "Init", "Level","Score", "Date/Time"));

        int count = 1;
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            long millis = rs.getLong(1);
            String initial = rs.getString(2);
            int score = rs.getInt(3);
            int level = rs.getInt(4);
            sb.append(String.format(Locale.getDefault(),
                    "%2d %8s %8s %8s %16s%n", count, initial, level, score, sdf.format(new Date(millis))));

            info.put(count,new Player(initial,level,score,sdf.format(new Date(millis))));
            count++;
        }
        rs.close();
        stmt.close();
        return sb.toString();
    }

    private void dialogMessage(final String s) {
        final EditText input = new EditText(mainActivity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(input);
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("please enter your initials (up to 3 characters):");
        builder.setPositiveButton("OK", (dialog, which) -> {
            final String inputName = input.getText().toString();
            mainActivity.doDatabaseInsert(inputName);
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> mainActivity.setResultsFromDatabase(s));
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
