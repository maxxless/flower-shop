import { Moment } from 'moment';
import { ICollectionInOrder } from 'app/shared/model/collection-in-order.model';
import { IFlowerInOrder } from 'app/shared/model/flower-in-order.model';
import { IUser } from 'app/shared/model/user.model';
import { IPacking } from 'app/shared/model/packing.model';
import { IDelivery } from 'app/shared/model/delivery.model';

export interface IOrder {
  id?: number;
  totalPrice?: number;
  date?: Moment;
  collectionDetails?: ICollectionInOrder[];
  flowerDetails?: IFlowerInOrder[];
  user?: IUser;
  packing?: IPacking;
  delivery?: IDelivery;
}

export const defaultValue: Readonly<IOrder> = {};
