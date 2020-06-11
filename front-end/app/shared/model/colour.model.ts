import { IFlower } from 'app/shared/model/flower.model';

export interface IColour {
  id?: number;
  name?: string;
  flowers?: IFlower[];
}

export const defaultValue: Readonly<IColour> = {};
