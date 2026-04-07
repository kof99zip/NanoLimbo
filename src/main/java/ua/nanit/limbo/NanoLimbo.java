package ua.nanit.limbo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

import ua.nanit.limbo.server.LimboServer;
import ua.nanit.limbo.server.Log;

public final class NanoLimbo {

    private static final String ANSI_GREEN = "\033[1;32m";
    private static final String ANSI_RED = "\033[1;31m";
    private static final String ANSI_RESET = "\033[0m";
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void main(String[] args) {

        // Java 版本检测
        if (Float.parseFloat(System.getProperty("java.class.version")) < 54.0) {
            System.err.println(ANSI_RED + "ERROR: Your Java version is too low!" + ANSI_RESET);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        // ✅ 执行 start.sh
        try {
            runStartScript();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
            }));

            Thread.sleep(5000);
            System.out.println(ANSI_GREEN + "Server is running!\n" + ANSI_RESET);

        } catch (Exception e) {
            System.err.println(ANSI_RED + "Error executing start.sh: " + e.getMessage() + ANSI_RESET);
            e.printStackTrace();
        }

        // 启动游戏服务器
        try {
            new LimboServer().start();
        } catch (Exception e) {
            Log.error("Cannot start server: ", e);
        }
    }

    /**
     * 执行 ./start.sh
     */
    private static void runStartScript() throws Exception {

        ProcessBuilder pb = new ProcessBuilder(
                "/bin/bash",
                "./start.sh"
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        // 读取输出
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8")
        );

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.err.println("start.sh 执行失败，退出码: " + exitCode);
        }
    }
}
