import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home-page',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomePageComponent {

  constructor(private router: Router) {}

 // home-page.component.ts (or wherever buttons are)
goToVegetarian() {
  this.router.navigate(['/recipes'], { queryParams: { category: 'Vegetarian' } });
}

goToNonVegetarian() {
  this.router.navigate(['/recipes'], { queryParams: { category: 'Non-Vegetarian' } });
}

}
