import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {PersonDetails} from "../../person-tabs/person-tabs.component";
import {DataService} from "../../service/data.service";
import {SidenavResponsiveComponent} from "../../sidenav-responsive/sidenav-responsive.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PaypalPaymentComponent} from "./paypal-payment/paypal-payment.component";
import {ProductCostElement} from "../../model/product-cost-element";

@Component({
  selector: 'app-person-details',
  templateUrl: './person-details.component.html',
  styleUrls: ['./person-details.component.css']
})
export class PersonDetailsComponent implements OnInit {
  firstFormGroup: any;
  secondFormGroup: any;
  isLinear = true;
  isDisabled = true;
  personDetails: PersonDetails;


  @Output() setOrderDateTime = new EventEmitter();
  @Output() refreshCart = new EventEmitter();
  @Output() setPdf = new EventEmitter();

  @Output() goForward = new EventEmitter();


  @Input() productCostList: ProductCostElement[];
  @Input() totalPay: string;

  @ViewChild(PaypalPaymentComponent) paypalPayment: PaypalPaymentComponent;

  constructor(private _data: DataService,
              private _sideNav: SidenavResponsiveComponent,
              private _snackBar: MatSnackBar,) {
  }

  ngOnInit(): void {

    this._data.getPersonDetails().subscribe(data => {
      this.personDetails = data;
    });
    this.firstFormGroup = new FormGroup({
      firstName: new FormControl('', Validators.required),
      lastName: new FormControl('', Validators.required)
    });
    this.secondFormGroup = new FormGroup({
      street: new FormControl('', Validators.required),
      city: new FormControl('', Validators.required),
      zip: new FormControl('', Validators.required),
      country: new FormControl('', Validators.required)
    });

  }

  saveDetails() {
    if (!this.firstFormGroup.pristine) {
      this.personDetails.firstName = this.firstFormGroup.value.firstName;
      this.personDetails.lastName = this.firstFormGroup.value.lastName;
    }
    if (!this.secondFormGroup.pristine) {
      this.personDetails.address.street = this.secondFormGroup.value.street;
      this.personDetails.address.zip = this.secondFormGroup.value.zip;
      this.personDetails.address.city = this.secondFormGroup.value.city;
      this.personDetails.address.country = this.secondFormGroup.value.country;
    }
    if (!this.firstFormGroup.pristine || !this.secondFormGroup.pristine) {
      this._data.savePersonDetails(this.personDetails).subscribe(d => {
        this._snackBar.open("Your details are updated successfully");
        this.firstFormGroup.pristine = true;
        this.secondFormGroup.pristine = true;
      });
    }
  }

  completeOrder() {
    this._data.getCompleteOrderTime().subscribe(data => {
      this.setOrderDateTime.emit(data.completed);
      console.log(data.pdf)
      this.setPdf.emit(data.pdf);
      this.refreshCart.emit();
      this.goForward.emit();
    });
  }
}



