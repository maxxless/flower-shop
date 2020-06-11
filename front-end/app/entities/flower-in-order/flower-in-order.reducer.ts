import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IFlowerInOrder, defaultValue } from 'app/shared/model/flower-in-order.model';

export const ACTION_TYPES = {
  SEARCH_FLOWERINORDERS: 'flowerInOrder/SEARCH_FLOWERINORDERS',
  FETCH_FLOWERINORDER_LIST: 'flowerInOrder/FETCH_FLOWERINORDER_LIST',
  FETCH_FLOWERINORDER: 'flowerInOrder/FETCH_FLOWERINORDER',
  CREATE_FLOWERINORDER: 'flowerInOrder/CREATE_FLOWERINORDER',
  UPDATE_FLOWERINORDER: 'flowerInOrder/UPDATE_FLOWERINORDER',
  DELETE_FLOWERINORDER: 'flowerInOrder/DELETE_FLOWERINORDER',
  RESET: 'flowerInOrder/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFlowerInOrder>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type FlowerInOrderState = Readonly<typeof initialState>;

// Reducer

export default (state: FlowerInOrderState = initialState, action): FlowerInOrderState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_FLOWERINORDERS):
    case REQUEST(ACTION_TYPES.FETCH_FLOWERINORDER_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FLOWERINORDER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_FLOWERINORDER):
    case REQUEST(ACTION_TYPES.UPDATE_FLOWERINORDER):
    case REQUEST(ACTION_TYPES.DELETE_FLOWERINORDER):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_FLOWERINORDERS):
    case FAILURE(ACTION_TYPES.FETCH_FLOWERINORDER_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FLOWERINORDER):
    case FAILURE(ACTION_TYPES.CREATE_FLOWERINORDER):
    case FAILURE(ACTION_TYPES.UPDATE_FLOWERINORDER):
    case FAILURE(ACTION_TYPES.DELETE_FLOWERINORDER):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_FLOWERINORDERS):
    case SUCCESS(ACTION_TYPES.FETCH_FLOWERINORDER_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_FLOWERINORDER):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_FLOWERINORDER):
    case SUCCESS(ACTION_TYPES.UPDATE_FLOWERINORDER):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_FLOWERINORDER):
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

const apiUrl = 'api/flower-in-orders';
const apiSearchUrl = 'api/_search/flower-in-orders';

// Actions

export const getSearchEntities: ICrudSearchAction<IFlowerInOrder> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_FLOWERINORDERS,
  payload: axios.get<IFlowerInOrder>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IFlowerInOrder> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_FLOWERINORDER_LIST,
  payload: axios.get<IFlowerInOrder>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IFlowerInOrder> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FLOWERINORDER,
    payload: axios.get<IFlowerInOrder>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IFlowerInOrder> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FLOWERINORDER,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IFlowerInOrder> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FLOWERINORDER,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFlowerInOrder> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FLOWERINORDER,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
