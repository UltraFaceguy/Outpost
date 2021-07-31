package land.face.jobbo;

import land.face.jobbo.managers.JobManager;
import lombok.Getter;

public class JobboApi {

  @Getter
  private final JobManager jobManager;

  public JobboApi(JobManager jobManager) {
    this.jobManager = jobManager;
  }

}
