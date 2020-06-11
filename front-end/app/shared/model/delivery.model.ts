import { IOrder } from 'app/shared/model/order.model';
import { IUser } from 'app/shared/model/user.model';
import { DeliveryType } from 'app/shared/model/enumerations/delivery-type.model';

export interface IDelivery {
  id?: number;
  address?: string;
  postOfficeNumber?: number;
  price?: number;
  type?: DeliveryType;
  order?: IOrder;
  user?: IUser;
}

export const defaultValue: Readonly<IDelivery> = {};
