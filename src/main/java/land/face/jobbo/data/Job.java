package land.face.jobbo.data;

import java.lang.ref.WeakReference;
import lombok.Getter;
import lombok.Setter;

public class Job {

  @Getter
  @Setter
  private int progress = 0;

  @Getter
  private final int progressCap;
  @Getter
  private final String taskType;
  @Getter
  private final String keyStringOne;
  @Getter
  private final String keyStringTwo;
  @Getter
  @Setter
  private int money, xp;

  private transient final WeakReference<JobBoard> board;
  private transient final WeakReference<JobTemplate> template;

  public Job(JobTemplate template, JobBoard board, String taskType, String keyStringOne,
      String keyStringTwo, int progressCap) {
    this.taskType = taskType;
    this.keyStringOne = keyStringOne;
    this.keyStringTwo = keyStringTwo;
    this.progressCap = progressCap;
    this.board = new WeakReference<>(board);
    this.template = new WeakReference<>(template);
  }

  public JobBoard getBoard() {
    return board.get();
  }

  public JobTemplate getTemplate() {
    return template.get();
  }

  public boolean addOne() {
    progress++;
    return isCompleted();
  }

  public boolean isCompleted() {
    return progress >= progressCap;
  }
}
