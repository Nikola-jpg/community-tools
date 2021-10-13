import { TaskStatus } from "./task-status.model";

export class UserTaskStatus {
    taskName: string;
    taskStatus: TaskStatus;

    constructor(taskName: string, status: TaskStatus) {
      this.taskName = taskName;
      this.taskStatus = status;
    }

  }
  