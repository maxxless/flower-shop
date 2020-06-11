import { IColour } from 'app/shared/model/colour.model';
import { IFlower } from 'app/shared/model/flower.model';
import { IOrder } from 'app/shared/model/order.model';

export interface IFlowerInOrder {
  id?: number;
  amount?: number;
  colour?: IColour;
  flower?: IFlower;
  order?: IOrder;
}

export const defaultValue: Readonly<IFlowerInOrder> = {};
