package co.grandcircus.cartapi;

public class CartItemsNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public CartItemsNotFoundException(String id) {
		super("Could not find Cart Items with id " + id);
	}
}
