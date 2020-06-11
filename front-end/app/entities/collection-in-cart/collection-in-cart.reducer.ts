import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ICollectionInCart, defaultValue } from 'app/shared/model/collection-in-cart.model';

export const ACTION_TYPES = {
  SEARCH_COLLECTIONINCARTS: 'collectionInCart/SEARCH_COLLECTIONINCARTS',
  FETCH_COLLECTIONINCART_LIST: 'collectionInCart/FETCH_COLLECTIONINCART_LIST',
  FETCH_COLLECTIONINCART: 'collectionInCart/FETCH_COLLECTIONINCART',
  CREATE_COLLECTIONINCART: 'collectionInCart/CREATE_COLLECTIONINCART',
  UPDATE_COLLECTIONINCART: 'collectionInCart/UPDATE_COLLECTIONINCART',
  DELETE_COLLECTIONINCART: 'collectionInCart/DELETE_COLLECTIONINCART',
  RESET: 'collectionInCart/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICollectionInCart>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type CollectionInCartState = Readonly<typeof initialState>;

// Reducer

export default (state: CollectionInCartState = initialState, action): CollectionInCartState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_COLLECTIONINCARTS):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTIONINCART_LIST):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTIONINCART):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_COLLECTIONINCART):
    case REQUEST(ACTION_TYPES.UPDATE_COLLECTIONINCART):
    case REQUEST(ACTION_TYPES.DELETE_COLLECTIONINCART):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_COLLECTIONINCARTS):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTIONINCART_LIST):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTIONINCART):
    case FAILURE(ACTION_TYPES.CREATE_COLLECTIONINCART):
    case FAILURE(ACTION_TYPES.UPDATE_COLLECTIONINCART):
    case FAILURE(ACTION_TYPES.DELETE_COLLECTIONINCART):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_COLLECTIONINCARTS):
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTIONINCART_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTIONINCART):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_COLLECTIONINCART):
    case SUCCESS(ACTION_TYPES.UPDATE_COLLECTIONINCART):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_COLLECTIONINCART):
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

const apiUrl = 'api/collection-in-carts';
const apiSearchUrl = 'api/_search/collection-in-carts';

// Actions

export const getSearchEntities: ICrudSearchAction<ICollectionInCart> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_COLLECTIONINCARTS,
  payload: axios.get<ICollectionInCart>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<ICollectionInCart> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_COLLECTIONINCART_LIST,
  payload: axios.get<ICollectionInCart>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ICollectionInCart> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_COLLECTIONINCART,
    payload: axios.get<ICollectionInCart>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ICollectionInCart> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_COLLECTIONINCART,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ICollectionInCart> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_COLLECTIONINCART,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICollectionInCart> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_COLLECTIONINCART,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
