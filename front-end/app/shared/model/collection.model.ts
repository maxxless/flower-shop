import { IPacking } from 'app/shared/model/packing.model';
import { IFlower } from 'app/shared/model/flower.model';
import { ICategory } from 'app/shared/model/category.model';

export interface ICollection {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  imageContentType?: string;
  image?: any;
  availablePackings?: IPacking[];
  flowers?: IFlower[];
  category?: ICategory;
}

export const defaultValue: Readonly<ICollection> = {};
