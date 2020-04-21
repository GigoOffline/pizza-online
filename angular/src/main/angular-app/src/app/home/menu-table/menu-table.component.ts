import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {animate, state, style, transition, trigger} from "@angular/animations";
import {DataService} from "../../service/data.service";
import {Creator} from "../../helper/creator";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatTableDataSource} from "@angular/material/table";
import {DomSanitizer} from "@angular/platform-browser";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatSort} from "@angular/material/sort";
import {SearchService} from "../../service/search.service";
import {SpinnerService} from "../../service/spinner.service";
import {CookieService} from 'ngx-cookie-service';

@Component({
  selector: 'app-menu-table',
  templateUrl: './menu-table.component.html',
  styleUrls: ['./menu-table.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class MenuTableComponent implements OnInit, AfterViewInit {

  pageSizeOptions = [5, 10, 20];
  array: any;


  myDataSource = new MatTableDataSource<ProductElement>();
  testData = `Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut
       labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco
       laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in
       voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat
       cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.`;

  columnsToDisplay = ['name', 'description', 'price', 'discount'];
  expandedElement: ProductElement | null;

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;


  constructor(private _data: DataService,
              private _sanitizer: DomSanitizer,
              private _snackBar: MatSnackBar,
              private _search: SearchService,
              private _spinner: SpinnerService,
              private _cookieService: CookieService,) {
  }

  ngOnInit() {
    this.fillTable();
  }

  ngAfterViewInit() {
    this._search.find.subscribe(data => {
      this.myDataSource.filter = data;
      return false;
    });
  }


  fillTable() {
    this._spinner.showSpinner();
    this._data.getAllProducts().subscribe(data => {
      this.myDataSource.data = data as ProductElement[];
      for (let i = 0; i < this.myDataSource.filteredData.length; i++) {
        this.myDataSource.filteredData[i].picture = 'data:image/jpg;base64,' + (this._sanitizer.bypassSecurityTrustResourceUrl(data[i].picture) as any)
          .changingThisBreaksApplicationSecurity;
        this.myDataSource.filteredData[i].price = this.createPrice(data[i].productCostList);
        this.myDataSource.filteredData[i].discount = data[i].productCostList.filter(x => x.discount > 0).length > 0 ? '%' : '';
      }
      this.setSort();
      this.setPaginator();
      this._spinner.stopSpinner();
    });
  }

  closeRow() {
    this.expandedElement = null;
  }

  createPrice(arr: any[]): string {
    let resultArr = arr.map(function (x) {
      return x.price;
    });
    let min = Math.min(...resultArr);
    let max = Math.max(...resultArr);
    return Creator.createPrice(min) + " - " + Creator.createPrice(max);
  }

  openSnackBar(message: string) {
    this._snackBar.open(message);
  }

  handlePage(event: PageEvent) {
    this._cookieService.set('cookie-pageIndex', event.pageIndex.toString());
    this._cookieService.set('cookie-pageSize', event.pageSize.toString());
    this._cookieService.set('cookie-length', event.length.toString());
  }

  changeSort() {
    // const now = new Date();
    // now.setMinutes(now.getMinutes() + 2);
    this._cookieService.set('cookie-active', this.sort.active);
    this._cookieService.set('cookie-direction', this.sort.direction);
  }

  setSort() {
    // this.sort.sort(<MatSortable>{
    //   id: this._cookieService.get('cookie-active'),
    //   start: this._cookieService.get('cookie-direction'),
    //   disableClear: true,
    // });
    this.sort.active = this._cookieService.get('cookie-active');
    this.sort.direction = this._cookieService.get('cookie-direction');

    this.myDataSource.sort = this.sort;
  }

  setPaginator() {
    const length =  this._cookieService.get('cookie-length');
    if(this.myDataSource.filteredData.length < Number(length)) {
      this._cookieService.delete('cookie-pageIndex');
      this._cookieService.delete('cookie-length');
    } else {
      this.paginator.length = Number(length);
      this.paginator.pageIndex = Number(this._cookieService.get('cookie-pageIndex'));
    }
    this.paginator.pageSize = Number(this._cookieService.get('cookie-pageSize'));
    this.myDataSource.paginator = this.paginator;
  }
}

export class ProductElement {
  id: number;
  name: string;
  productCostList: [];
  price: any;
  description: string;
  picture: string;
  discount: string;
}
