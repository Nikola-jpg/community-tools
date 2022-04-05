import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UsersService } from 'src/app/services/users.service';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  users: User[];
  userLimit: number;
  daysFetch: number;
  sort: string;

  constructor(private usersService: UsersService, private activatedRoute:ActivatedRoute) { }

  ngOnInit(): void {
    this.activatedRoute.queryParamMap
    .subscribe(params => {
      // @ts-ignore
      this.userLimit = +params.get('userLimit')||null;
    });

    this.getUsers(this.userLimit);
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

}
