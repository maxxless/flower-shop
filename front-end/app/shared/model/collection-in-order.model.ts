import { ICollection } from 'app/shared/model/collection.model';
import { IPacking } from 'app/shared/model/packing.model';
import { IOrder } from 'app/shared/model/order.model';

export interface ICollectionInOrder {
  id?: number;
  amount?: number;
  collection?: ICollection;
  packing?: IPacking;
  order?: IOrder;
}

export const defaultValue: Readonly<ICollectionInOrder> = {};
