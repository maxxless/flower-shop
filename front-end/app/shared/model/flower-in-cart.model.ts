import { IColour } from 'app/shared/model/colour.model';
import { IFlower } from 'app/shared/model/flower.model';
import { ICart } from 'app/shared/model/cart.model';

export interface IFlowerInCart {
  id?: number;
  amount?: number;
  colour?: IColour;
  flower?: IFlower;
  cart?: ICart;
}

export const defaultValue: Readonly<IFlowerInCart> = {};
