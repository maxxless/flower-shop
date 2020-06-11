import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IColour, defaultValue } from 'app/shared/model/colour.model';

export const ACTION_TYPES = {
  SEARCH_COLOURS: 'colour/SEARCH_COLOURS',
  FETCH_COLOUR_LIST: 'colour/FETCH_COLOUR_LIST',
  FETCH_COLOUR: 'colour/FETCH_COLOUR',
  CREATE_COLOUR: 'colour/CREATE_COLOUR',
  UPDATE_COLOUR: 'colour/UPDATE_COLOUR',
  DELETE_COLOUR: 'colour/DELETE_COLOUR',
  RESET: 'colour/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IColour>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false
};

export type ColourState = Readonly<typeof initialState>;

// Reducer

export default (state: ColourState = initialState, action): ColourState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_COLOURS):
    case REQUEST(ACTION_TYPES.FETCH_COLOUR_LIST):
    case REQUEST(ACTION_TYPES.FETCH_COLOUR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_COLOUR):
    case REQUEST(ACTION_TYPES.UPDATE_COLOUR):
    case REQUEST(ACTION_TYPES.DELETE_COLOUR):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_COLOURS):
    case FAILURE(ACTION_TYPES.FETCH_COLOUR_LIST):
    case FAILURE(ACTION_TYPES.FETCH_COLOUR):
    case FAILURE(ACTION_TYPES.CREATE_COLOUR):
    case FAILURE(ACTION_TYPES.UPDATE_COLOUR):
    case FAILURE(ACTION_TYPES.DELETE_COLOUR):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_COLOURS):
    case SUCCESS(ACTION_TYPES.FETCH_COLOUR_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_COLOUR):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_COLOUR):
    case SUCCESS(ACTION_TYPES.UPDATE_COLOUR):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_COLOUR):
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

const apiUrl = 'api/colours';
const apiSearchUrl = 'api/_search/colours';

// Actions

export const getSearchEntities: ICrudSearchAction<IColour> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_COLOURS,
  payload: axios.get<IColour>(`${apiSearchUrl}?query=${query}`)
});

export const getEntities: ICrudGetAllAction<IColour> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_COLOUR_LIST,
  payload: axios.get<IColour>(`${apiUrl}?cacheBuster=${new Date().getTime()}`)
});

export const getEntity: ICrudGetAction<IColour> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_COLOUR,
    payload: axios.get<IColour>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<IColour> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_COLOUR,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IColour> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_COLOUR,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IColour> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_COLOUR,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
