package api.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEnpointInterface {
    Object post(BaseModel baseModel);
    Object get();
    Object update(long id,BaseModel baseModel);
    Object delete(long id);
}
