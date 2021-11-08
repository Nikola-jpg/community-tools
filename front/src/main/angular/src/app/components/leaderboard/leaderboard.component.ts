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
      // @ts-ignore
      this.daysFetch = +params.get('daysFetch')||null;
      // @ts-ignore
      this.sort = +params.get('sort')||null;
    });

    this.getUsers(this.userLimit, this.daysFetch, this.sort);
  }

  getUsers(userLimit: number, daysFetch: number, sort: string): void {
    this.usersService.getRestUsers(userLimit, daysFetch, sort).subscribe(
      data => {
        this.users = data;
      });

  }

}
