package com.lanternadonorte;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Aplicador de papel de parede e tela inicial alternativa, sem coleta de dados
 * e sem depender de serviços externos.
 */
public final class MainActivity extends Activity {
    private static final int IVORY = Color.rgb(250, 245, 226);
    private static final int GOLD = Color.rgb(231, 190, 98);
    private static final int ICE = Color.rgb(183, 222, 225);
    private static final int INK = Color.rgb(7, 21, 30);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(INK);
        if (isHomeLaunch()) {
            showHome();
        } else {
            showThemePanel();
        }
    }

    private boolean isHomeLaunch() {
        Intent intent = getIntent();
        return Intent.ACTION_MAIN.equals(intent.getAction())
                && intent.hasCategory(Intent.CATEGORY_HOME);
    }

    private void showThemePanel() {
        FrameLayout root = artRoot();
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content = column(22);
        content.setPadding(dp(24), dp(56), dp(24), dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        TextView eyebrow = label("TEMA ORIGINAL · FANTASIA DE INVERNO", 12, GOLD);
        eyebrow.setLetterSpacing(.14f);
        content.addView(eyebrow);

        TextView title = label("Lanterna\ndo Norte", 40, IVORY);
        title.setGravity(Gravity.START);
        title.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD));
        LinearLayout.LayoutParams titleParams = verticalParams(-2, -2, 8);
        content.addView(title, titleParams);

        TextView intro = label("Uma floresta sob neve, luz dourada e céu azul-profundo. "
                + "O tema troca o seu papel de parede e pode oferecer uma tela inicial própria.", 17, ICE);
        intro.setLineSpacing(dp(3), 1f);
        content.addView(intro, verticalParams(-1, -2, 20));

        LinearLayout card = card();
        TextView cardTitle = label("O que será aplicado", 18, IVORY);
        cardTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        card.addView(cardTitle);
        card.addView(label("• Papel de parede para tela inicial e bloqueio\n"
                        + "• Paleta azul-gelo, pinho e ouro\n"
                        + "• Tela inicial alternativa, se você escolher ativá-la", 15, ICE),
                verticalParams(-1, -2, 4));
        content.addView(card, verticalParams(-1, -2, 20));

        TextView apply = primaryButton("Aplicar papel de parede", true);
        apply.setOnClickListener(v -> applyWallpaper());
        content.addView(apply, verticalParams(-1, dp(54), 12));

        TextView home = primaryButton("Usar como tela inicial", false);
        home.setOnClickListener(v -> openHomeSettings());
        content.addView(home, verticalParams(-1, dp(54), 12));

        TextView note = label("O launcher padrão da Motorola não permite que outro app altere "
                + "seus ícones. Ao escolher “Lanterna do Norte” como tela inicial, os seus "
                + "apps continuam os mesmos, exibidos em uma grade com moldura temática.", 13, ICE);
        note.setLineSpacing(dp(2), 1f);
        content.addView(note, verticalParams(-1, -2, 8));

        root.addView(scroll);
        setContentView(root);
    }

    private void showHome() {
        FrameLayout root = artRoot();
        LinearLayout content = column(10);
        content.setPadding(dp(22), dp(50), dp(22), dp(12));

        TextView time = label(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()), 42, IVORY);
        time.setTypeface(android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD));
        content.addView(time);
        TextView sub = label("O Portal do Norte", 14, GOLD);
        sub.setLetterSpacing(.12f);
        content.addView(sub, verticalParams(-2, -2, 8));

        TextView rule = new TextView(this);
        rule.setBackgroundColor(Color.argb(125, 231, 190, 98));
        content.addView(rule, verticalParams(-1, dp(1), 12));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setUseDefaultMargins(false);
        List<AppEntry> apps = installedApps();
        int max = Math.min(apps.size(), 24);
        for (int i = 0; i < max; i++) addAppTile(grid, apps.get(i));
        content.addView(grid, verticalParams(-1, 0, 0, 1f));

        TextView settings = primaryButton("✦  Tema e papel de parede", false);
        settings.setTextSize(14);
        settings.setOnClickListener(v -> {
            Intent open = new Intent(this, MainActivity.class);
            open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(open);
        });
        content.addView(settings, verticalParams(-1, dp(46), 0));

        root.addView(content, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
    }

    private FrameLayout artRoot() {
        FrameLayout root = new FrameLayout(this);
        root.addView(new WinterArtView(this), new FrameLayout.LayoutParams(-1, -1));
        return root;
    }

    private LinearLayout column(int spacing) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.START);
        return layout;
    }

    private TextView label(String text, float size, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        return view;
    }

    private LinearLayout card() {
        LinearLayout card = column(5);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.argb(190, 9, 36, 45));
        background.setCornerRadius(dp(20));
        background.setStroke(dp(1), Color.argb(120, 183, 222, 225));
        card.setBackground(background);
        return card;
    }

    private TextView primaryButton(String text, boolean filled) {
        TextView button = label(text, 16, filled ? INK : IVORY);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(28));
        if (filled) {
            background.setColor(GOLD);
        } else {
            background.setColor(Color.argb(178, 8, 34, 43));
            background.setStroke(dp(1), Color.argb(190, 231, 190, 98));
        }
        button.setBackground(background);
        button.setClickable(true);
        return button;
    }

    private LinearLayout.LayoutParams verticalParams(int width, int height, int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.topMargin = dp(top);
        return params;
    }

    private LinearLayout.LayoutParams verticalParams(int width, int height, int top, float weight) {
        LinearLayout.LayoutParams params = verticalParams(width, height, top);
        params.weight = weight;
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void applyWallpaper() {
        Toast.makeText(this, "Preparando a floresta de inverno…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            try {
                Bitmap artwork = WinterArt.makeWallpaper();
                WallpaperManager manager = WallpaperManager.getInstance(this);
                try {
                    manager.setBitmap(artwork, null, true,
                            WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                } catch (IOException lockUnavailable) {
                    manager.setBitmap(artwork, null, true, WallpaperManager.FLAG_SYSTEM);
                }
                runOnUiThread(() -> Toast.makeText(this,
                        "Tema aplicado à tela inicial e, quando disponível, à tela bloqueada.",
                        Toast.LENGTH_LONG).show());
            } catch (Exception error) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Não foi possível aplicar o papel de parede: " + error.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void openHomeSettings() {
        try {
            startActivity(new Intent(Settings.ACTION_HOME_SETTINGS));
            Toast.makeText(this, "Selecione “Lanterna do Norte” na lista.", Toast.LENGTH_LONG).show();
        } catch (Exception unavailable) {
            Toast.makeText(this, "Abra Configurações › Apps › Apps padrão › Tela inicial.", Toast.LENGTH_LONG).show();
        }
    }

    private List<AppEntry> installedApps() {
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> matches = pm.queryIntentActivities(main, PackageManager.MATCH_ALL);
        ArrayList<AppEntry> apps = new ArrayList<>();
        for (ResolveInfo info : matches) {
            ActivityInfo activity = info.activityInfo;
            if (activity == null || getPackageName().equals(activity.packageName)) continue;
            CharSequence title = info.loadLabel(pm);
            if (title == null || title.length() == 0) title = activity.packageName;
            apps.add(new AppEntry(activity.packageName, activity.name, title.toString(), info.loadIcon(pm)));
        }
        Collections.sort(apps, Comparator.comparing(entry -> entry.name.toLowerCase(Locale.getDefault())));
        return apps;
    }

    private void addAppTile(GridLayout grid, AppEntry app) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_HORIZONTAL);
        tile.setPadding(dp(3), dp(6), dp(3), dp(6));
        ImageView icon = new ImageView(this);
        icon.setImageDrawable(app.icon);
        icon.setPadding(dp(9), dp(9), dp(9), dp(9));
        GradientDrawable plate = new GradientDrawable();
        plate.setShape(GradientDrawable.OVAL);
        plate.setColor(Color.argb(200, 234, 244, 239));
        plate.setStroke(dp(2), Color.argb(215, 231, 190, 98));
        icon.setBackground(plate);
        tile.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(58)));
        TextView name = label(shortName(app.name), 11, IVORY);
        name.setGravity(Gravity.CENTER);
        name.setSingleLine(true);
        tile.addView(name, new LinearLayout.LayoutParams(-1, dp(22)));
        tile.setOnClickListener(v -> launch(app));
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        params.setMargins(0, 0, 0, dp(4));
        grid.addView(tile, params);
    }

    private String shortName(String name) {
        return name.length() <= 13 ? name : name.substring(0, 12) + "…";
    }

    private void launch(AppEntry app) {
        try {
            Intent open = new Intent(Intent.ACTION_MAIN);
            open.addCategory(Intent.CATEGORY_LAUNCHER);
            open.setComponent(new ComponentName(app.packageName, app.className));
            open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(open);
        } catch (Exception ignored) {
            Toast.makeText(this, "Não foi possível abrir " + app.name, Toast.LENGTH_SHORT).show();
        }
    }

    private static final class AppEntry {
        final String packageName;
        final String className;
        final String name;
        final Drawable icon;
        AppEntry(String packageName, String className, String name, Drawable icon) {
            this.packageName = packageName;
            this.className = className;
            this.name = name;
            this.icon = icon;
        }
    }

    private static final class WinterArtView extends View {
        WinterArtView(Context context) { super(context); }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            WinterArt.draw(canvas, getWidth(), getHeight());
        }
    }

    /** Desenha arte original: céu, pinheiros, neve e uma lanterna. */
    private static final class WinterArt {
        private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

        static Bitmap makeWallpaper() {
            Bitmap bitmap = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888);
            draw(new Canvas(bitmap), bitmap.getWidth(), bitmap.getHeight());
            return bitmap;
        }

        static void draw(Canvas canvas, int width, int height) {
            float sx = width / 1080f;
            float sy = height / 2400f;
            canvas.save();
            canvas.scale(sx, sy);
            PAINT.setStyle(Paint.Style.FILL);
            PAINT.setShader(new LinearGradient(0, 0, 0, 2400,
                    new int[] {Color.rgb(4, 22, 39), Color.rgb(12, 59, 73), Color.rgb(22, 96, 105)},
                    new float[] {0f, .5f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, 1080, 2400, PAINT);
            PAINT.setShader(null);

            Random random = new Random(417219);
            for (int i = 0; i < 155; i++) {
                float x = random.nextInt(1080);
                float y = 40 + random.nextInt(1250);
                float r = 1 + random.nextFloat() * 2.8f;
                PAINT.setColor(Color.argb(110 + random.nextInt(130), 235, 247, 244));
                canvas.drawCircle(x, y, r, PAINT);
            }
            PAINT.setColor(Color.argb(215, 224, 245, 239));
            canvas.drawCircle(828, 282, 78, PAINT);
            PAINT.setColor(Color.argb(85, 213, 241, 238));
            canvas.drawCircle(828, 282, 116, PAINT);

            mountain(canvas, 0, 1220, 325, 840, 640, 1180, 960, 830, 1080, 1150, Color.rgb(20, 77, 90));
            mountain(canvas, 0, 1400, 250, 1070, 470, 1340, 700, 970, 960, 1380, 1080, 1160, Color.rgb(13, 54, 66));
            PAINT.setColor(Color.rgb(7, 45, 52));
            canvas.drawRect(0, 1350, 1080, 1910, PAINT);

            int[] trees = {55, 140, 230, 335, 440, 565, 690, 780, 890, 995};
            for (int i = 0; i < trees.length; i++) {
                float h = 400 + (i % 3) * 95;
                pine(canvas, trees[i], 1760 - h, 110 + (i % 2) * 28, h, Color.rgb(4, 37, 42));
            }
            pine(canvas, 98, 1375, 180, 570, Color.rgb(3, 31, 37));
            pine(canvas, 980, 1325, 185, 620, Color.rgb(3, 31, 37));

            PAINT.setColor(Color.rgb(218, 236, 232));
            Path snow = new Path();
            snow.moveTo(0, 1810); snow.cubicTo(215, 1725, 362, 1905, 536, 1815);
            snow.cubicTo(756, 1700, 892, 1825, 1080, 1750); snow.lineTo(1080, 2400); snow.lineTo(0, 2400); snow.close();
            canvas.drawPath(snow, PAINT);
            PAINT.setColor(Color.rgb(166, 212, 216));
            Path road = new Path();
            road.moveTo(473, 2400); road.lineTo(718, 2400); road.lineTo(620, 1810); road.lineTo(563, 1810); road.close();
            canvas.drawPath(road, PAINT);

            drawLantern(canvas, 432, 1475);
            random = new Random(892);
            for (int i = 0; i < 90; i++) {
                float x = random.nextInt(1080); float y = 1120 + random.nextInt(1220);
                float r = 1 + random.nextFloat() * 4;
                PAINT.setColor(Color.argb(85 + random.nextInt(120), 246, 252, 250));
                canvas.drawCircle(x, y, r, PAINT);
            }

            PAINT.setShader(new LinearGradient(0, 0, 0, 2400,
                    new int[] {Color.argb(90, 0, 0, 0), Color.TRANSPARENT, Color.argb(115, 0, 12, 20)},
                    new float[] {0f, .45f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, 1080, 2400, PAINT);
            PAINT.setShader(null);
            canvas.restore();
        }

        private static void mountain(Canvas canvas, float... values) {
            int color = (int) values[values.length - 1];
            Path path = new Path();
            path.moveTo(values[0], values[1]);
            for (int i = 2; i < values.length - 1; i += 2) path.lineTo(values[i], values[i + 1]);
            path.lineTo(1080, 1600); path.lineTo(0, 1600); path.close();
            PAINT.setColor(color); canvas.drawPath(path, PAINT);
        }

        private static void pine(Canvas canvas, float x, float y, float width, float height, int color) {
            PAINT.setColor(color);
            canvas.drawRect(x - width * .09f, y + height * .62f, x + width * .09f, y + height, PAINT);
            for (int level = 0; level < 4; level++) {
                float top = y + level * height * .18f;
                float half = width * (.32f + level * .18f);
                Path branch = new Path();
                branch.moveTo(x, top - height * .08f);
                branch.lineTo(x - half, top + height * .34f);
                branch.lineTo(x + half, top + height * .34f);
                branch.close(); canvas.drawPath(branch, PAINT);
            }
        }

        private static void drawLantern(Canvas canvas, float x, float y) {
            PAINT.setColor(Color.argb(55, 255, 209, 93));
            for (int r = 210; r >= 70; r -= 28) {
                PAINT.setColor(Color.argb(8, 255, 215, 105));
                canvas.drawCircle(x + 55, y + 250, r, PAINT);
            }
            PAINT.setColor(Color.rgb(45, 36, 25));
            canvas.drawRect(x + 48, y, x + 65, y + 470, PAINT);
            PAINT.setColor(Color.rgb(199, 158, 66));
            canvas.drawRect(x + 6, y + 200, x + 108, y + 207, PAINT);
            canvas.drawRect(x + 10, y + 362, x + 104, y + 370, PAINT);
            PAINT.setColor(Color.rgb(238, 199, 92));
            canvas.drawRect(x + 18, y + 215, x + 96, y + 356, PAINT);
            PAINT.setColor(Color.rgb(255, 239, 174));
            canvas.drawRect(x + 28, y + 226, x + 86, y + 345, PAINT);
            PAINT.setStyle(Paint.Style.STROKE); PAINT.setStrokeWidth(12); PAINT.setColor(Color.rgb(199, 158, 66));
            canvas.drawArc(x + 24, y + 158, x + 90, y + 234, 185, 170, false, PAINT);
            PAINT.setStyle(Paint.Style.FILL);
        }
    }
}
