import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/models/user.model';
import { UsersService } from 'src/app/services/users.service';

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  users: User[];

  constructor(private usersService: UsersService) { }

  ngOnInit(): void {
    this.getUsers();
  }

  getUsers(): void {
    this.usersService.getRestUsers().subscribe(
      data => {
        this.users = data;
      });

  }

}
