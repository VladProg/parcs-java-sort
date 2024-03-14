import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import parcs.*;

public class Sort implements AM {
    private static long startTime = 0;
    public static void startTimer() {
        startTime = System.nanoTime();
    }
    public static void stopTimer() {
        if (startTime == 0) {
            System.err.println("Timer was not started.");
            return;
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        double seconds = timeElapsed / 1_000_000_000.0;
        System.err.println("Time passed: " + seconds + " seconds.");
    }

    public static void main(String[] args) throws Exception {
        System.err.println("Preparing...");
        startTimer();
        if (args.length != 1) {
            System.err.println("Usage: Sort <number-of-workers>");
            System.exit(1);
        }

        int k = Integer.parseInt(args[0]);

        task curtask = new task();
        curtask.addJarFile("Sort.jar");
        AMInfo info = new AMInfo(curtask, null);
        stopTimer();

        System.err.println("Reading input...");
        startTimer();
        int[] arr = readInput();
        int n = arr.length;
        stopTimer();

        System.err.println("Forwarding parts to workers...");
        startTimer();
        channel[] channels = new channel[k];
        for (int i = 0; i < k; i++) {
            int l = n * i / k, r = n * (i + 1) / k;
            int[] part = new int[r - l];
            System.arraycopy(arr, l, part, 0, part.length);
            point p = info.createPoint();
            channel c = p.createChannel();
            p.execute("Sort");
            c.write(part);
            channels[i] = c;
        }
        stopTimer();

        System.err.println("Getting results from workers...");
        startTimer();
        int[][] parts = new int[k][];
        for (int i = 0; i < k; i++) {
            parts[i] = (int[])channels[i].readObject();
        }
        stopTimer();

        System.err.println("Merging...");
        startTimer();
        arr = merge(parts);
        stopTimer();

        System.err.println("Printing result...");
        startTimer();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        for (int item : arr)
            writer.write(item + "\n");
        writer.close();
        stopTimer();
        
        curtask.end();
    }

    public static int[] readInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(br.readLine());
        int[] arr = new int[n];

        for (int i = 0; i < n; i++)
            arr[i] = Integer.parseInt(br.readLine());
        br.close();

        return arr;
    }

    public void run(AMInfo info) {
        System.err.println("Getting part from parent...");
        startTimer();
        int[] arr = (int[])info.parent.readObject();
        stopTimer();

        System.err.println("Sorting array with " + arr.length + " elements...");
        startTimer();
        Arrays.sort(arr);
        stopTimer();

        System.out.println("Sending sorted part to parent...");
        startTimer();
        info.parent.write(arr);
        stopTimer();

        System.out.println("Done.");
    }

    public static int[] merge(int[][] parts) {
        int totalSize = 0;
        for (int[] part : parts)
            totalSize += part.length;

        int[] arr = new int[totalSize];
        int[] indices = new int[parts.length];
        for (int i = 0; i < totalSize; i++) {
            int minValue = Integer.MAX_VALUE;
            int chosenIndex = -1;
            for (int j = 0; j < parts.length; j++)
                if (indices[j] < parts[j].length && parts[j][indices[j]] <= minValue) {
                    minValue = parts[j][indices[j]];
                    chosenIndex = j;
                }
            arr[i] = minValue;
            indices[chosenIndex]++;
        }
        return arr;
    }
}
