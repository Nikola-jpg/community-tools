import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UsersService } from 'src/app/services/users.service';
import { TasksService } from 'src/app/services/tasks.service';
import { UserTaskStatus } from 'src/app/models/user-task-status.model';

@Component({
  selector: 'app-task-status',
  templateUrl: './task-status.component.html',
  styleUrls: ['./task-status.component.css']
})
export class TaskStatusComponent implements OnInit {

  tasks: string[];

  users: User[];

  constructor(private tasksService: TasksService, private usersService: UsersService) {
  }

  ngOnInit(): void {
    this.getTasks();
    this.getUsers();
  }

  getTasks(): void {
    this.tasksService.getRestTasks().subscribe(
      data => {
        this.tasks = data;
      });
  }

  getUsers(): void {
    this.usersService.getRestUsers().subscribe(
      data => {
        this.users = data;
      });

  }

  getClass(user: User, task: string): any {
    const status = user?.taskStatuses?.find(element => element.taskName === task)?.taskStatus;
    return {
      'pull_request': status === 'pull request',
      'done': status === 'done',
      'changes_requested': status === 'changes requested',
      'ready_for_review': status === 'ready for review'
    }
  }
}