import { combineReducers } from 'redux';
import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import authentication, { AuthenticationState } from './authentication';
import applicationProfile, { ApplicationProfileState } from './application-profile';

import administration, { AdministrationState } from 'app/modules/administration/administration.reducer';
import userManagement, { UserManagementState } from 'app/modules/administration/user-management/user-management.reducer';
import register, { RegisterState } from 'app/modules/account/register/register.reducer';
import activate, { ActivateState } from 'app/modules/account/activate/activate.reducer';
import password, { PasswordState } from 'app/modules/account/password/password.reducer';
import settings, { SettingsState } from 'app/modules/account/settings/settings.reducer';
import passwordReset, { PasswordResetState } from 'app/modules/account/password-reset/password-reset.reducer';
// prettier-ignore
import collection, {
  CollectionState
} from 'app/entities/collection/collection.reducer';
// prettier-ignore
import category, {
  CategoryState
} from 'app/entities/category/category.reducer';
// prettier-ignore
import cart, {
  CartState
} from 'app/entities/cart/cart.reducer';
// prettier-ignore
import colour, {
  ColourState
} from 'app/entities/colour/colour.reducer';
// prettier-ignore
import flower, {
  FlowerState
} from 'app/entities/flower/flower.reducer';
// prettier-ignore
import order, {
  OrderState
} from 'app/entities/order/order.reducer';
// prettier-ignore
import delivery, {
  DeliveryState
} from 'app/entities/delivery/delivery.reducer';
// prettier-ignore
import clientCard, {
  ClientCardState
} from 'app/entities/client-card/client-card.reducer';
// prettier-ignore
import packing, {
  PackingState
} from 'app/entities/packing/packing.reducer';
// prettier-ignore
import flowerInOrder, {
  FlowerInOrderState
} from 'app/entities/flower-in-order/flower-in-order.reducer';
// prettier-ignore
import collectionInOrder, {
  CollectionInOrderState
} from 'app/entities/collection-in-order/collection-in-order.reducer';
// prettier-ignore
import flowerInCart, {
  FlowerInCartState
} from 'app/entities/flower-in-cart/flower-in-cart.reducer';
// prettier-ignore
import collectionInCart, {
  CollectionInCartState
} from 'app/entities/collection-in-cart/collection-in-cart.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

export interface IRootState {
  readonly authentication: AuthenticationState;
  readonly applicationProfile: ApplicationProfileState;
  readonly administration: AdministrationState;
  readonly userManagement: UserManagementState;
  readonly register: RegisterState;
  readonly activate: ActivateState;
  readonly passwordReset: PasswordResetState;
  readonly password: PasswordState;
  readonly settings: SettingsState;
  readonly collection: CollectionState;
  readonly category: CategoryState;
  readonly cart: CartState;
  readonly colour: ColourState;
  readonly flower: FlowerState;
  readonly order: OrderState;
  readonly delivery: DeliveryState;
  readonly clientCard: ClientCardState;
  readonly packing: PackingState;
  readonly flowerInOrder: FlowerInOrderState;
  readonly collectionInOrder: CollectionInOrderState;
  readonly flowerInCart: FlowerInCartState;
  readonly collectionInCart: CollectionInCartState;
  /* jhipster-needle-add-reducer-type - JHipster will add reducer type here */
  readonly loadingBar: any;
}

const rootReducer = combineReducers<IRootState>({
  authentication,
  applicationProfile,
  administration,
  userManagement,
  register,
  activate,
  passwordReset,
  password,
  settings,
  collection,
  category,
  cart,
  colour,
  flower,
  order,
  delivery,
  clientCard,
  packing,
  flowerInOrder,
  collectionInOrder,
  flowerInCart,
  collectionInCart,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar
});

export default rootReducer;
