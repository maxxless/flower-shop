import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ICollectionInOrder, defaultValue } from 'app/shared/model/collection-in-order.model';

export const ACTION_TYPES = {
  SEARCH_COLLECTIONINORDERS: 'collectionInOrder/SEARCH_COLLECTIONINORDERS',
  FETCH_COLLECTIONINORDER_LIST: 'collectionInOrder/FETCH_COLLECTIONINORDER_LIST',
  FETCH_COLLECTIONINORDER: 'collectionInOrder/FETCH_COLLECTIONINORDER',
  CREATE_COLLECTIONINORDER: 'collectionInOrder/CREATE_COLLECTIONINORDER',
  UPDATE_COLLECTIONINORDER: 'collectionInOrder/UPDATE_COLLECTIONINORDER',
  DELETE_COLLECTIONINORDER: 'collectionInOrder/DELETE_COLLECTIONINORDER',
  RESET: 'collectionInOrder/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICollectionInOrder>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type CollectionInOrderState = Readonly<typeof initialState>;

// Reducer

export default (state: CollectionInOrderState = initialState, action): CollectionInOrderState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_COLLECTIONINORDERS):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTIONINORDER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_COLLECTIONINORDER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_COLLECTIONINORDER):
    case REQUEST(ACTION_TYPES.UPDATE_COLLECTIONINORDER):
    case REQUEST(ACTION_TYPES.DELETE_COLLECTIONINORDER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_COLLECTIONINORDERS):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTIONINORDER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_COLLECTIONINORDER):
    case FAILURE(ACTION_TYPES.CREATE_COLLECTIONINORDER):
    case FAILURE(ACTION_TYPES.UPDATE_COLLECTIONINORDER):
    case FAILURE(ACTION_TYPES.DELETE_COLLECTIONINORDER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_COLLECTIONINORDERS):
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTIONINORDER_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_COLLECTIONINORDER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_COLLECTIONINORDER):
    case SUCCESS(ACTION_TYPES.UPDATE_COLLECTIONINORDER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_COLLECTIONINORDER):
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

const apiUrl = 'api/collection-in-orders';
const apiSearchUrl = 'api/_search/collection-in-orders';

// Actions

export const getSearchEntities: ICrudSearchAction<ICollectionInOrder> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_COLLECTIONINORDERS,
  payload: axios.get<ICollectionInOrder>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<ICollectionInOrder> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_COLLECTIONINORDER_LIST,
  payload: axios.get<ICollectionInOrder>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<ICollectionInOrder> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_COLLECTIONINORDER,
    payload: axios.get<ICollectionInOrder>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ICollectionInOrder> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_COLLECTIONINORDER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ICollectionInOrder> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_COLLECTIONINORDER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<ICollectionInOrder> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_COLLECTIONINORDER,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
