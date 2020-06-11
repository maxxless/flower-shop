import { IUser } from 'app/shared/model/user.model';
import { ICollectionInCart } from 'app/shared/model/collection-in-cart.model';
import { IFlowerInCart } from 'app/shared/model/flower-in-cart.model';

export interface ICart {
  id?: number;
  totalPriceWithoutDiscount?: number;
  cardDiscount?: number;
  bonusDiscount?: number;
  finalPrice?: number;
  count?: number;
  user?: IUser;
  collectionDetails?: ICollectionInCart[];
  flowerDetails?: IFlowerInCart[];
}

export const defaultValue: Readonly<ICart> = {};
