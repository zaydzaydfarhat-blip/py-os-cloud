package com.pyos.cloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(19, 29, 47);
    private final int panel = Color.rgb(30, 45, 68);
    private final int accent = Color.rgb(48, 105, 170);
    private LinearLayout root, desktop, taskbar;
    private TextView clock, status;
    private String notes = "";
    private int tapScore = 0;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(12, 20, 34));
        getWindow().setNavigationBarColor(Color.BLACK);
        notes = getPreferences(MODE_PRIVATE).getString("notes", "");
        showDesktop();
    }

    private TextView label(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        t.setPadding(10, 8, 10, 8);
        return t;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
        return b;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(14, 12, 14, 12);
        l.setBackgroundColor(bg);
        return l;
    }

    private void showDesktop() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);

        FrameLayout workspace = new FrameLayout(this);
        desktop = column();
        desktop.setBackgroundColor(bg);
        desktop.addView(label("PY-OS CLOUD", 27));
        desktop.addView(label("Native Android Edition • 0.4.0", 13));

        String[][] apps = {
            {"📁", "Files"}, {"🐍", "Terminal"}, {"📝", "Notes"},
            {"🔢", "Calculator"}, {"🌐", "Browser"}, {"🎮", "Games"},
            {"⚙️", "Settings"}, {"☁️", "Backup"}
        };
        for (String[] app : apps) {
            Button b = button(app[0] + "   " + app[1]);
            b.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            b.setOnClickListener(v -> openApp(app[1]));
            desktop.addView(b, new LinearLayout.LayoutParams(-1, 52));
        }
        workspace.addView(desktop);
        root.addView(workspace, new LinearLayout.LayoutParams(-1, 0, 1));

        taskbar = new LinearLayout(this);
        taskbar.setGravity(Gravity.CENTER_VERTICAL);
        taskbar.setPadding(7, 3, 7, 3);
        taskbar.setBackgroundColor(Color.rgb(10, 16, 27));
        Button start = button("⊞ Start");
        start.setOnClickListener(v -> showStart());
        taskbar.addView(start);
        status = label("Desktop", 12);
        taskbar.addView(status, new LinearLayout.LayoutParams(0, -2, 1));
        clock = label("", 13);
        taskbar.addView(clock);
        root.addView(taskbar, new LinearLayout.LayoutParams(-1, 55));
        setContentView(root);
        updateClock();
    }

    private void updateClock() {
        if (clock == null) return;
        clock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        clock.postDelayed(this::updateClock, 15000);
    }

    private void showStart() {
        String[] names = {"Files", "Terminal", "Notes", "Calculator", "Browser", "Games", "Settings", "Backup"};
        new AlertDialog.Builder(this).setTitle("PY-OS Start")
            .setItems(names, (d, which) -> openApp(names[which])).show();
    }

    private void openApp(String name) {
        LinearLayout page = column();
        page.setBackgroundColor(panel);
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = label(name, 22);
        header.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        Button close = button("⌂ Desktop");
        close.setOnClickListener(v -> showDesktop());
        header.addView(close);
        page.addView(header);
        ScrollView scroll = new ScrollView(this);
        LinearLayout body = column();
        body.setBackgroundColor(panel);
        scroll.addView(body);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        switch (name) {
            case "Files": files(body); break;
            case "Terminal": terminal(body); break;
            case "Notes": notes(body); break;
            case "Calculator": calculator(body); break;
            case "Browser": browser(body); break;
            case "Games": games(body); break;
            case "Settings": settings(body); break;
            case "Backup": backup(body); break;
            default: body.addView(label("PY-OS Cloud", 24));
        }
        FrameLayout frame = new FrameLayout(this);
        frame.setBackgroundColor(bg);
        frame.addView(page);
        root.removeViewAt(0);
        root.addView(frame, 0, new LinearLayout.LayoutParams(-1, 0, 1));
        if (status != null) status.setText(name);
    }

    private void files(LinearLayout body) {
        body.addView(label("Android file picker", 20));
        body.addView(label("Choose a file from your device. PY-OS does not scan your storage.", 14));
        Button pick = button("Choose file");
        pick.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("*/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, 10);
        });
        body.addView(pick);
    }

    private void terminal(LinearLayout body) {
        body.setBackgroundColor(Color.BLACK);
        TextView output = label("PY-OS Shell 0.4.0\nPython-inspired command mode.\nType help.\n", 14);
        output.setTextColor(Color.rgb(100, 240, 140));
        body.addView(output);
        EditText command = new EditText(this);
        command.setSingleLine(true);
        command.setTextColor(Color.WHITE);
        command.setHintTextColor(Color.LTGRAY);
        command.setHint("pyos > command");
        body.addView(command);
        Button run = button("Run command");
        body.addView(run);
        run.setOnClickListener(v -> {
            String cmd = command.getText().toString().trim();
            String result;
            if (cmd.equals("help")) result = "help, date, version, apps, clear, echo TEXT, note TEXT";
            else if (cmd.equals("date")) result = new Date().toString();
            else if (cmd.equals("version")) result = "PY-OS Cloud Android 0.4.0";
            else if (cmd.equals("apps")) result = "Files | Terminal | Notes | Calculator | Browser | Games | Backup";
            else if (cmd.equals("clear")) { output.setText(""); command.setText(""); return; }
            else if (cmd.startsWith("echo ")) result = cmd.substring(5);
            else if (cmd.startsWith("note ")) { notes = cmd.substring(5); saveNotes(); result = "Note saved."; }
            else result = "Unknown command. Type help.";
            output.append("\npyos > " + cmd + "\n" + result + "\n");
            command.setText("");
        });
    }

    private void notes(LinearLayout body) {
        body.addView(label("Your notes save automatically on this device.", 14));
        EditText editor = new EditText(this);
        editor.setGravity(Gravity.TOP);
        editor.setMinLines(10);
        editor.setTextColor(Color.WHITE);
        editor.setHint("Write something...");
        editor.setText(notes);
        body.addView(editor, new LinearLayout.LayoutParams(-1, -2));
        Button save = button("Save notes");
        save.setOnClickListener(v -> {
            notes = editor.getText().toString();
            saveNotes();
            Toast.makeText(this, "Notes saved", Toast.LENGTH_SHORT).show();
        });
        body.addView(save);
    }

    private void saveNotes() {
        getPreferences(MODE_PRIVATE).edit().putString("notes", notes).apply();
    }

    private void calculator(LinearLayout body) {
        body.addView(label("Basic calculator", 20));
        EditText a = new EditText(this); a.setHint("First number"); a.setInputType(8194);
        EditText b = new EditText(this); b.setHint("Second number"); b.setInputType(8194);
        body.addView(a); body.addView(b);
        TextView result = label("Result: ", 20); body.addView(result);
        String[] ops = {"+", "−", "×", "÷"};
        for (String op : ops) {
            Button btn = button(op);
            btn.setOnClickListener(v -> {
                try {
                    double x = Double.parseDouble(a.getText().toString());
                    double y = Double.parseDouble(b.getText().toString());
                    if (op.equals("÷") && y == 0) { result.setText("Cannot divide by zero"); return; }
                    double r = op.equals("+") ? x+y : op.equals("−") ? x-y : op.equals("×") ? x*y : x/y;
                    result.setText("Result: " + r);
                } catch (Exception e) { result.setText("Enter two valid numbers."); }
            });
            body.addView(btn);
        }
    }

    private void browser(LinearLayout body) {
        body.addView(label("Open a website in your browser", 18));
        EditText address = new EditText(this);
        address.setHint("https://example.com");
        body.addView(address);
        Button go = button("Open website");
        go.setOnClickListener(v -> {
            String url = address.getText().toString().trim();
            if (!url.startsWith("https://") && !url.startsWith("http://")) url = "https://" + url;
            try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
            catch (Exception e) { Toast.makeText(this, "Could not open website", Toast.LENGTH_SHORT).show(); }
        });
        body.addView(go);
        body.addView(label("Some websites cannot be embedded in other apps. This opens your installed browser.", 13));
    }

    private void games(LinearLayout body) {
        body.addView(label("Mini Games", 22));
        TextView score = label("Tap score: 0", 20);
        body.addView(score);
        Button tap = button("TAP!");
        tap.setOnClickListener(v -> { tapScore++; score.setText("Tap score: " + tapScore); });
        body.addView(tap);
        Button reset = button("Reset score");
        reset.setOnClickListener(v -> { tapScore = 0; score.setText("Tap score: 0"); });
        body.addView(reset);
    }

    private void settings(LinearLayout body) {
        body.addView(label("Settings", 22));
        body.addView(label("PY-OS Cloud Android Edition", 14));
        Button dark = button("Dark theme");
        dark.setOnClickListener(v -> body.setBackgroundColor(Color.BLACK));
        body.addView(dark);
        Button blue = button("Default theme");
        blue.setOnClickListener(v -> body.setBackgroundColor(panel));
        body.addView(blue);
    }

    private void backup(LinearLayout body) {
        body.addView(label("PY-OS portable backup", 20));
        body.addView(label("Export or import your notes as a .pycloud file.", 14));
        Button export = button("Export .pycloud backup");
        export.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.setType("application/json");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.putExtra(Intent.EXTRA_TITLE, "pyos-backup.pycloud");
            startActivityForResult(i, 20);
        });
        body.addView(export);
        Button imp = button("Import .pycloud backup");
        imp.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("*/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, 21);
        });
        body.addView(imp);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try {
            if (requestCode == 20) {
                JSONObject backup = new JSONObject();
                backup.put("format", "pycloud");
                backup.put("version", 1);
                backup.put("app", "PY-OS Cloud");
                backup.put("notes", notes);
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    out.write(backup.toString(2).getBytes("UTF-8"));
                }
                Toast.makeText(this, "Backup exported", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 21) {
                StringBuilder json = new StringBuilder();
                try (InputStream in = getContentResolver().openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    String line;
                    while ((line = reader.readLine()) != null) json.append(line);
                }
                JSONObject backup = new JSONObject(json.toString());
                if (!"pycloud".equals(backup.optString("format"))) throw new Exception("Not a PY-OS backup");
                notes = backup.optString("notes", "");
                saveNotes();
                Toast.makeText(this, "Backup imported", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 10) {
                Toast.makeText(this, "Selected: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onBackPressed() {
        showDesktop();
    }
}
