import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
        <footer><span>© {{year}} secinto GmbH All Rights Reserved</span></footer>
  `,
  styleUrls: ['footer.component.css'],
})
export class FooterComponent implements OnInit {

  year: number;
  constructor() { }

  ngOnInit() {
      this.year = new Date().getFullYear();
  }

}