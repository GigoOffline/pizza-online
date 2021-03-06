import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {DataService} from "../../../service/data.service";
import {SidenavResponsiveComponent} from "../../../sidenav-responsive/sidenav-responsive.component";
import {faEuroSign} from "@fortawesome/free-solid-svg-icons";
import {PriceService} from "../../../service/price.service";


@Component({
  selector: 'app-add-to-cart-form',
  templateUrl: './add-to-cart-form.component.html',
  styleUrls: ['./add-to-cart-form.component.css']
})
export class AddToCartFormComponent implements OnInit {

  @Input('product-cost-list') productCostList: any[];

  @Input() elementName: string;

  @Output() closeRow = new EventEmitter();

  @Output() openSnackBar = new EventEmitter();

  isSubmitButtonHidden = true;
  isNormalPriceHidden = true;
  quantity = 1;

  faEuro = faEuroSign;

  formDiscountPrice: string;
  formNormalPrice: string;
  discount: number;


  addToCartForm = new FormGroup({
    productCost: new FormControl('', Validators.required),
    quantity: new FormControl('', Validators.required),
  });

  constructor(private _data: DataService,
              private _sideNav: SidenavResponsiveComponent,
              private _price: PriceService) {
  }

  createRange(n: number) {
    return Array(n);
  }

  ngOnInit(): void {
  }

  onSubmit() {
    const body = {productCostId: this.addToCartForm.value.productCost.id, quantity: this.addToCartForm.value.quantity};
    this._data.addProductToCart(body).subscribe((d) => {
      // this.closeRow.emit();
      const message = this.addToCartForm.value.quantity + " x " + this.elementName + ", " + this.addToCartForm.value.productCost.property + " was added to cart";
      this.openSnackBar.emit(message);
      this.resetForm();
      this._sideNav.cart = d;
    });
  }

  onChange(val: any) {
    this.discount = val?.discount;
    if (this.discount != undefined) {
      if (this.discount == 0) {
        this.formDiscountPrice = this._price.convertToPriceWithComma(val.price);
        this.isNormalPriceHidden = true;
      } else {
        const price = val?.price;
        this.formDiscountPrice = this._price.convertToPriceWithComma(price - price * this.discount / 100);
        this.formNormalPrice = this._price.convertToPriceWithComma(price);
        this.isNormalPriceHidden = false;
      }
    } else {
      this.formDiscountPrice = undefined;
      this.isNormalPriceHidden = true;
    }
    this.isSubmitButtonHidden = false;
  }

  private resetForm() {
    this.addToCartForm.reset({
      quantity: 1
    });
    Object.keys(this.addToCartForm.controls).forEach(key => {
      this.addToCartForm.get(key).setErrors(null);
    });
    this.isSubmitButtonHidden = true;
  }
}
