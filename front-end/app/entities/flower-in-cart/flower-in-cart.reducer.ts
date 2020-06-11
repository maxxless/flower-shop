import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IFlowerInCart, defaultValue } from 'app/shared/model/flower-in-cart.model';

export const ACTION_TYPES = {
  SEARCH_FLOWERINCARTS: 'flowerInCart/SEARCH_FLOWERINCARTS',
  FETCH_FLOWERINCART_LIST: 'flowerInCart/FETCH_FLOWERINCART_LIST',
  FETCH_FLOWERINCART: 'flowerInCart/FETCH_FLOWERINCART',
  CREATE_FLOWERINCART: 'flowerInCart/CREATE_FLOWERINCART',
  UPDATE_FLOWERINCART: 'flowerInCart/UPDATE_FLOWERINCART',
  DELETE_FLOWERINCART: 'flowerInCart/DELETE_FLOWERINCART',
  RESET: 'flowerInCart/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFlowerInCart>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type FlowerInCartState = Readonly<typeof initialState>;

// Reducer

export default (state: FlowerInCartState = initialState, action): FlowerInCartState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_FLOWERINCARTS):
    case REQUEST(ACTION_TYPES.FETCH_FLOWERINCART_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FLOWERINCART):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_FLOWERINCART):
    case REQUEST(ACTION_TYPES.UPDATE_FLOWERINCART):
    case REQUEST(ACTION_TYPES.DELETE_FLOWERINCART):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_FLOWERINCARTS):
    case FAILURE(ACTION_TYPES.FETCH_FLOWERINCART_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FLOWERINCART):
    case FAILURE(ACTION_TYPES.CREATE_FLOWERINCART):
    case FAILURE(ACTION_TYPES.UPDATE_FLOWERINCART):
    case FAILURE(ACTION_TYPES.DELETE_FLOWERINCART):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_FLOWERINCARTS):
    case SUCCESS(ACTION_TYPES.FETCH_FLOWERINCART_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOWERINCART):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_FLOWERINCART):
    case SUCCESS(ACTION_TYPES.UPDATE_FLOWERINCART):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_FLOWERINCART):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/flower-in-carts';
const apiSearchUrl = 'api/_search/flower-in-carts';

// Actions

export const getSearchEntities: ICrudSearchAction<IFlowerInCart> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_FLOWERINCARTS,
  payload: axios.get<IFlowerInCart>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IFlowerInCart> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_FLOWERINCART_LIST,
  payload: axios.get<IFlowerInCart>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IFlowerInCart> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FLOWERINCART,
    payload: axios.get<IFlowerInCart>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IFlowerInCart> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FLOWERINCART,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IFlowerInCart> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FLOWERINCART,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFlowerInCart> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FLOWERINCART,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
