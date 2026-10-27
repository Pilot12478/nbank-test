package api.skelethon.interfaces;

import api.models.dto.BaseModel;

public interface CrudEnpointInterface {
    Object post(BaseModel baseModel);
    Object get();
    Object update(long id,BaseModel baseModel);
    Object delete(long id);
}
