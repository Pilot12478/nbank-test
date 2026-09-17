package requests.skelethon.interfaces;

import models.BaseModel;

public interface CrudEnpointInterface {
    Object post(BaseModel baseModel);
    Object get();
    Object update(long id,BaseModel baseModel);
    Object delete(long id);
}
