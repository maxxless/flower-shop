import { ICollection } from 'app/shared/model/collection.model';

export interface IPacking {
  id?: number;
  name?: string;
  material?: string;
  price?: number;
  collections?: ICollection[];
}

export const defaultValue: Readonly<IPacking> = {};
