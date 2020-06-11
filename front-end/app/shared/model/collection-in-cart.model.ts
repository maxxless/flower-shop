import { ICollection } from 'app/shared/model/collection.model';
import { IPacking } from 'app/shared/model/packing.model';
import { ICart } from 'app/shared/model/cart.model';

export interface ICollectionInCart {
  id?: number;
  amount?: number;
  collection?: ICollection;
  packing?: IPacking;
  cart?: ICart;
}

export const defaultValue: Readonly<ICollectionInCart> = {};
