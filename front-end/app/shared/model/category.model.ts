import { ICollection } from 'app/shared/model/collection.model';

export interface ICategory {
  id?: number;
  name?: string;
  collections?: ICollection[];
}

export const defaultValue: Readonly<ICategory> = {};
