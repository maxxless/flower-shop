import { IUser } from 'app/shared/model/user.model';
import { CardType } from 'app/shared/model/enumerations/card-type.model';

export interface IClientCard {
  id?: number;
  name?: string;
  description?: string;
  type?: CardType;
  bonusAmount?: number;
  percentage?: number;
  user?: IUser;
}

export const defaultValue: Readonly<IClientCard> = {};
