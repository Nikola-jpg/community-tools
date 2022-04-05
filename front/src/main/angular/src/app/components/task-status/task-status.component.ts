import {Component, OnInit} from '@angular/core';
import {User} from 'src/app/models/user.model';
import {UsersService} from 'src/app/services/users.service';
import {TasksService} from 'src/app/services/tasks.service';
import {ActivatedRoute} from "@angular/router";
import {UserTaskStatus} from "../../models/user-task-status.model";

@Component({
  selector: 'app-task-status',
  templateUrl: './task-status.component.html',
  styleUrls: ['./task-status.component.css']
})
export class TaskStatusComponent implements OnInit {

  tasks: string[];
  users: User[];
  userLimit: number;
  daysFetch: number;
  sort: string;


  constructor(private tasksService: TasksService, private usersService: UsersService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams
    .subscribe(params => {
      this.userLimit = params.userLimit;
    });
    this.getTasks();
    this.getUsers(this.userLimit);
  }

  getTasks(): void {
    this.tasksService.getRestTasks().subscribe(
      data => {
        this.tasks = data;
      });
  }

  getUsers(userLimit: number): void {
    this.usersService.getRestUsers(userLimit).subscribe(
      data => {
        this.getSortedUsers(data);
      });

  }

  getSortedUsers(users : User[]): void {
      this.users = users.sort((u1,u2) => {
        if (u1.dateLastActivity > u2.dateLastActivity) {
          return -1;
        }
        if (u1.dateLastActivity < u2.dateLastActivity) {
          return 1;
        }
        return 0;
      });
    }

  isTaskStatusEquals(element: UserTaskStatus, task: string): boolean {
    return element.taskName === task ||
      element.taskName.replace('/', '.') === task;
  }

  getClass(user: User, task: string): any {
    const status =
      user?.taskStatuses?.find(element => this.isTaskStatusEquals(element, task))?.taskStatus;
    return {
      'pull_request': status === 'pull request',
      'done': status === 'done',
      'changes_requested': status === 'changes requested',
      'ready_for_review': status === 'ready for review'
    }
  }
}
