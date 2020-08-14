import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {DataService} from "../service/data.service";
import {SidenavResponsiveComponent} from "../sidenav-responsive/sidenav-responsive.component";
import {MatStepper} from "@angular/material/stepper";
import {TransactionItem, UnitAmount} from "./person-details/paypal-payment/paypal-payment.component";
import {PersonDetailsComponent} from "./person-details/person-details.component";
import {SpinnerService} from "../service/spinner.service";
import {PriceService} from "../service/price.service";
import {ProductCostElement} from "../model/product-cost-element";

@Component({
  selector: 'app-cart-stepper',
  templateUrl: './cart-stepper.component.html',
  styleUrls: ['./cart-stepper.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class CartStepperComponent implements OnInit {

  isLinear = true;
  orderDateTime: string;
  productCostList: ProductCostElement[];
  totalPay: string;
  totalQuantity: number;
  transactionItems: TransactionItem[];

  @ViewChild('stepper') stepper: MatStepper;

  @ViewChild(PersonDetailsComponent) personDetails: PersonDetailsComponent;

  constructor(private _data: DataService,
              private _sideNav: SidenavResponsiveComponent,
              private _spinner: SpinnerService,
              private _price: PriceService) {
  }

  ngOnInit(): void {
  }

  public setOrderDateTime(event) {
    this.orderDateTime = event;
  }

  public getProductsInCart() {
    this._spinner.showSpinner();
    this._data.getCartProductCosts().subscribe(data => {
      this.productCostList = data;
      this._sideNav.cart = this._price.calculateTotalQuantity(data);
      this.createTotal(this.productCostList);
      this._spinner.stopSpinner();
      return this.productCostList;
    });
  }

  public createTotal(array: ProductCostElement[]) {
    this.totalQuantity = this._price.calculateTotalQuantity(array);
    let totalPrice = this._price.calculateTotalPrice(array);
    this.totalPay = this._price.convertToPriceWithComma(totalPrice);
  }

  attachZero(num: number): string {
    const str = '' + num;
    return str.length == 1 ? '0' + str : str;
  }

  goForward() {
    this.stepper.next();
  }

  public createTransactionItems() {
    let priceService = this._price;
    this.transactionItems = this.productCostList.map(function (element) {
      let amount = new UnitAmount();
      amount.currency_code = 'EUR';
      amount.value = priceService.calculatePriceWithDiscount(element.price, element.discount) + '';
      let item = new TransactionItem();
      item.name = element.product.name;
      item.unit_amount = amount;
      item.quantity = element.quantity + '';
      return item;
    });
    this.personDetails.paypalPayment.initConfig(this.totalPay, this.transactionItems);
  }
}

