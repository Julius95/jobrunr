package org.jobrunr.stubs;

import org.jobrunr.jobs.JobContext;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.filters.ApplyStateFilter;
import org.jobrunr.jobs.filters.ElectStateFilter;
import org.jobrunr.jobs.filters.JobServerFilter;
import org.jobrunr.jobs.states.JobState;
import org.jobrunr.scheduling.BackgroundJob;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TestService implements TestServiceInterface {

    private static int processedJobs = 0;

    public int getProcessedJobs() {
        return processedJobs;
    }

    public void doWorkWithFile(File file) throws Exception {
        System.out.println("Doing some work... " + file.getAbsolutePath());
    }

    public void doWorkWithPath(Path path) throws Exception {
        System.out.println("Doing some work... " + path.toFile().getAbsolutePath());
    }

    public void doWork(Work work) throws Exception {
        processedJobs += work.workCount;
        System.out.println("Doing some work... " + work.workCount + "; " + work.someString);
    }

    public void doWork(Double count) {
        System.out.println("Doing some work... " + processedJobs + count);
    }

    public void doWork(Integer count) {
        processedJobs += count;
        System.out.println("Doing some work... " + processedJobs);
    }

    public void doWork(Integer count, JobContext jobContext) {
        processedJobs += count;
        System.out.println("Doing some work... " + processedJobs + "; jobId: " + jobContext.getJobId());
        jobContext.getMetadata().put("test", "test");
    }

    public void doWork(int countA, int countB) {
        processedJobs += (countA + countB);
        System.out.println("Doing some work... " + processedJobs);
    }

    @Job(name = "Doing some hard work for user %1")
    public void doWorkWithAnnotation(Integer userId, String userName) {
        System.out.println("Doing some work... " + processedJobs);
    }

    public void doWork(int count, String aString, Instant instant) {
        processedJobs += count;
        System.out.println("Doing some work... " + processedJobs + " " + aString + " " + instant);
    }

    public void doWork(UUID uuid) {
        System.out.println("Doing some work... " + uuid);
    }

    public void doWork(UUID uuid, int count, Instant instant) {
        processedJobs += count;
        System.out.println("Doing some work... " + processedJobs + " " + uuid + " " + instant);
    }

    public void doWork(String aString, int count, Instant instant) {
        processedJobs += count;
        System.out.println("Doing some work... " + processedJobs + " " + aString + " " + instant);
    }

    public void doWork(LocalDateTime localDateTime) {
        System.out.println("Doing some work... " + processedJobs + " " + localDateTime.toString());
    }

    public void doWork(boolean bool, int i, long l, float f, double d) {
        System.out.println("Doing some work... " + bool + "; " + i + "; " + l + "; " + f + "; " + d);
    }

    public void doWork(byte b, short s, char c) {
        System.out.println("Doing some work... " + b + "; " + s + "; " + c);
    }

    @Job(name = "Doing some work")
    public void doWork() {
        processedJobs++;
        System.out.println("Doing some work... " + processedJobs);
    }

    @Job(jobFilters = {TheSunIsAlwaysShiningElectStateFilter.class, TestFilter.class})
    public void doWorkWithCustomJobFilters() {
        System.out.println("I will always succeed thanks to my SunIsAlwaysShiningElectStateFilter... ");
    }

    public String doWorkAndReturnResult(String someString) {
        return "Hello to you to " + someString;
    }

    public void doWorkThatFails() {
        processedJobs++;
        System.out.println("Whoopsie, an error will occur " + processedJobs);
        throw new RuntimeException("Whoopsie, an error occcured");
    }

    public void doWorkThatTakesLong() {
        doWorkThatTakesLong(25 + ThreadLocalRandom.current().nextInt(0, 5));
    }

    public void doWorkThatTakesLong(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void doWorkThatTakesLong(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void scheduleNewWork(int amount) {
        for (int i = 0; i < amount; i++) {
            int finalI = i;
            BackgroundJob.enqueue(() -> doWork(finalI));
        }
    }

    public void scheduleNewWorkSlowly(int amount) {
        try {
            for (int i = 0; i < amount; i++) {
                int finalI = i;
                BackgroundJob.enqueue(() -> doWork(finalI));
                Thread.sleep(10000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        processedJobs = 0;
    }

    public UUID getAnUUID() {
        return UUID.randomUUID();
    }

    public static class Work {

        private int workCount;
        private String someString;
        private UUID uuid;

        private Work() {

        }

        public Work(int workCount, String someString, UUID uuid) {
            this.workCount = workCount;
            this.someString = someString;
            this.uuid = uuid;
        }

        public int getWorkCount() {
            return workCount;
        }

        public String getSomeString() {
            return someString;
        }

        public UUID getUuid() {
            return uuid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Work work = (Work) o;

            if (workCount != work.workCount) return false;
            if (!Objects.equals(someString, work.someString)) return false;
            return Objects.equals(uuid, work.uuid);
        }

        @Override
        public int hashCode() {
            int result = workCount;
            result = 31 * result + (someString != null ? someString.hashCode() : 0);
            result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
            return result;
        }

        public static Work from(int workCount, String someString, UUID uuid) {
            return new Work(workCount, someString, uuid);
        }
    }

    public static class TheSunIsAlwaysShiningElectStateFilter implements ElectStateFilter {

        @Override
        public void onStateElection(org.jobrunr.jobs.Job job, JobState newState) {
            job.succeeded();
        }
    }

    public static class TestFilter implements JobServerFilter, ApplyStateFilter {

        @Override
        public void onStateApplied(org.jobrunr.jobs.Job job, JobState oldState, JobState newState) {
            getMetadata(job).put("onStateApplied", null);
        }

        @Override
        public void onProcessing(org.jobrunr.jobs.Job job) {
            getMetadata(job).put("onProcessing", null);
        }

        @Override
        public void onProcessed(org.jobrunr.jobs.Job job) {
            getMetadata(job).put("onProcessed", null);
        }

        private Map<String, Object> getMetadata(org.jobrunr.jobs.Job job) {
            return Whitebox.getInternalState(job, "metadata");
        }
    }
}
