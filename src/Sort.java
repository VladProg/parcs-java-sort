import java.io.*;
import java.util.*;

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
        arr = mergeLog(parts);
        stopTimer();

        System.err.println("Printing result...");
        startTimer();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        for (int i = 0; i <= 99; i++)
            writer.write(arr[(int) ((long)(arr.length - 1) * i / 99)] + "\n");
        writer.close();
        stopTimer();
        
        curtask.end();
    }

    public static int[] readInput() {
        Scanner scanner = new Scanner(System.in);

        int n = scanner.nextInt();
        int seed = scanner.nextInt();

        int[] arr = new int[n];
        Random rng = new Random(seed);
        for (int i = 0; i < n; i++)
            arr[i] = rng.nextInt();

        scanner.close();

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

    public static int[] mergeLinear(int[][] parts) {
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

    static class HeapItem implements Comparable<HeapItem> {
        int[] part;
        int index;

        public HeapItem(int[] part) {
            this.part = part;
            this.index = 0;
        }

        public void next() {
            index++;
        }

        public int get() {
            return part[index];
        }

        public boolean has() {
            return index < part.length;
        }

        @Override
        public int compareTo(HeapItem other) {
            return Integer.compare(this.get(), other.get());
        }
    }

    public static int[] mergeLog(int[][] parts) {
        int totalLength = 0;
        for (int[] part : parts)
            totalLength += part.length;
        int[] arr = new int[totalLength];

        PriorityQueue<HeapItem> pq = new PriorityQueue<>();
        for (int[] part : parts) {
            HeapItem item = new HeapItem(part);
            if (item.has())
                pq.add(item);
        }

        for (int i = 0; i < totalLength; i++) {
            HeapItem item = pq.poll();
            arr[i] = item.get();
            item.next();
            if (item.has())
                pq.add(item);
        }
        return arr;
    }
}
