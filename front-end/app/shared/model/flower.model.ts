import { IColour } from 'app/shared/model/colour.model';
import { ICollection } from 'app/shared/model/collection.model';

export interface IFlower {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  imageContentType?: string;
  image?: any;
  availableColours?: IColour[];
  collectionsIns?: ICollection[];
}

export const defaultValue: Readonly<IFlower> = {};
