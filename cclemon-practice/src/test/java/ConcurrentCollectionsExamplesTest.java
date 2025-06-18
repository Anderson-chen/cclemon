import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.*;

public class ConcurrentCollectionsExamplesTest {

    private static final int THREADS = 10;
    private static final int OPERATIONS = 1000;

    private void runThreads(List<Runnable> tasks) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (Runnable r : tasks) {
            Thread t = new Thread(r);
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    // ===== ArrayList 不安全，使用 synchronized 包裝安全 =====
    @Test
    void testArrayListUnsafe_shouldFail() throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                try {
                    for (int j = 0; j < OPERATIONS; j++) {
                        list.add(tid * OPERATIONS + j);
                    }
                } catch (Exception e) {
                    errorOccurred.set(true);
                }
            });
        }

        runThreads(tasks);

        // 可能資料不完整或例外發生
        assertThat(list.size()).isLessThan(THREADS * OPERATIONS).withFailMessage("資料應該不完整或出錯");
        assertThat(errorOccurred.get()).isFalse(); // 不一定會例外，但會資料不完整
    }

    @Test
    void testArrayListSafe_withSynchronizedList() throws InterruptedException {
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    list.add(tid * OPERATIONS + j);
                }
            });
        }

        runThreads(tasks);

        assertThat(list).hasSize(THREADS * OPERATIONS);
    }

    // ===== CopyOnWriteArrayList 安全示範 =====
    @Test
    void testCopyOnWriteArrayListSafe() throws InterruptedException {
        List<Integer> list = new CopyOnWriteArrayList<>();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    list.add(tid * OPERATIONS + j);
                }
            });
        }

        runThreads(tasks);

        assertThat(list).hasSize(THREADS * OPERATIONS);
    }

    // ===== HashMap 不安全，使用 synchronized 包裝安全 =====
    @Test
    void testHashMapUnsafe_shouldFail() throws InterruptedException {
        Map<Integer, String> map = new HashMap<>();
        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                try {
                    for (int j = 0; j < OPERATIONS; j++) {
                        map.put(tid * OPERATIONS + j, "v" + j);
                    }
                } catch (Exception e) {
                    errorOccurred.set(true);
                }
            });
        }

        runThreads(tasks);

        // size 可能不對或發生例外
        assertThat(map.size()).isLessThan(THREADS * OPERATIONS).withFailMessage("資料不完整或發生錯誤");
        assertThat(errorOccurred.get()).isFalse(); // 不一定會例外，但結果可能錯
    }

    @Test
    void testHashMapSafe_withSynchronizedMap() throws InterruptedException {
        Map<Integer, String> map = Collections.synchronizedMap(new HashMap<>());

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    map.put(tid * OPERATIONS + j, "v" + j);
                }
            });
        }

        runThreads(tasks);

        assertThat(map).hasSize(THREADS * OPERATIONS);
    }

    // ===== ConcurrentHashMap 安全示範 =====
    @Test
    void testConcurrentHashMapSafe() throws InterruptedException {
        Map<Integer, String> map = new ConcurrentHashMap<>();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    map.put(tid * OPERATIONS + j, "v" + j);
                }
            });
        }

        runThreads(tasks);

        assertThat(map).hasSize(THREADS * OPERATIONS);
    }

    // ===== ConcurrentLinkedQueue 安全示範 =====
    @Test
    void testConcurrentLinkedQueueSafe() throws InterruptedException {
        Queue<Integer> queue = new ConcurrentLinkedQueue<>();

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    queue.add(tid * OPERATIONS + j);
                }
            });
        }

        runThreads(tasks);

        assertThat(queue).hasSize(THREADS * OPERATIONS);
    }

    // ===== LinkedList 非執行緒安全，無同步可能失敗 =====
    @Test
    void testLinkedListUnsafe_shouldFail() throws InterruptedException {
        Queue<Integer> queue = new LinkedList<>();
        AtomicBoolean errorOccurred = new AtomicBoolean(false);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            tasks.add(() -> {
                try {
                    for (int j = 0; j < OPERATIONS; j++) {
                        queue.add(tid * OPERATIONS + j);
                    }
                } catch (Exception e) {
                    errorOccurred.set(true);
                }
            });
        }

        runThreads(tasks);

        // size 可能不對或發生例外
        assertThat(queue.size()).isLessThan(THREADS * OPERATIONS).withFailMessage("資料不完整或發生錯誤");
        assertThat(errorOccurred.get()).isFalse(); // 不一定會例外，但結果可能錯
    }

    // ===== LinkedBlockingQueue 安全示範 =====
    @Test
    void testLinkedBlockingQueueSafe() throws InterruptedException {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();

        List<Runnable> producers = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            int tid = i;
            producers.add(() -> {
                for (int j = 0; j < OPERATIONS; j++) {
                    try {
                        queue.put(tid * OPERATIONS + j);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        runThreads(producers);

        assertThat(queue).hasSize(THREADS * OPERATIONS);
    }

    // ===== SynchronousQueue 正反測試 =====
    @Test
    void testSynchronousQueueSafe() throws InterruptedException {
        SynchronousQueue<String> queue = new SynchronousQueue<>();

        AtomicBoolean success = new AtomicBoolean(false);

        Thread producer = new Thread(() -> {
            try {
                queue.put("data");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                String val = queue.take();
                if ("data".equals(val)) success.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertThat(success.get()).isTrue();
    }

    // ===== DelayQueue 基本示範 =====
    @Test
    void testDelayQueueBasic() throws InterruptedException {
        class DelayedTask implements Delayed {
            private final long startTime = System.currentTimeMillis() + 1000;

            @Override
            public long getDelay(TimeUnit unit) {
                return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            }

            @Override
            public int compareTo(Delayed o) {
                return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
            }
        }

        DelayQueue<DelayedTask> queue = new DelayQueue<>();
        queue.put(new DelayedTask());

        long before = System.currentTimeMillis();
        DelayedTask task = queue.take();
        long after = System.currentTimeMillis();

        assertThat(after - before).isGreaterThanOrEqualTo(900);
        assertThat(task).isNotNull();
    }
}





