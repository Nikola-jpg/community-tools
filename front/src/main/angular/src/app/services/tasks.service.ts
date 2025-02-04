import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TasksService {

  private defaultApi: string = `${environment.apiURL}/api/tasks`;

  constructor (private http: HttpClient) {  }

  getRestTasks(): Observable<string[]> {
    return this.http.get<string[]>(this.defaultApi);
  }

}
