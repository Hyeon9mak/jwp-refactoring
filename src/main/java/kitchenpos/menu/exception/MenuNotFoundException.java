package kitchenpos.menu.exception;

import kitchenpos.common.exception.KitchenposException;

public class MenuNotFoundException extends KitchenposException {

    public MenuNotFoundException(String message) {
        super(message);
    }
}
