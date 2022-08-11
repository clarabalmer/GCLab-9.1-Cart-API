package co.grandcircus.cartapi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@Autowired
	private CartItemsRepository cartRepo;
	
	@GetMapping("/reset")
	public String reset() {
		cartRepo.deleteAll();
		CartItems cart = new CartItems("Ramen", .30, 10);
		cartRepo.insert(cart);
		cart = new CartItems("Cashews", 4.99, 2);
		cartRepo.insert(cart);
		cart = new CartItems("Spinach", 1.90, 4);
		cartRepo.insert(cart);
		cart = new CartItems("Sushi", 8.99, 1);
		cartRepo.insert(cart);
		return "Data reset";
	}
	
	@GetMapping("/cart-items")
	public List<CartItems> readAll(@RequestParam(required=false) String product, 
			@RequestParam(required=false) Double maxPrice, @RequestParam(required=false) String prefix, 
			@RequestParam(required=false) Integer pageSize) {
		if (product != null) {
			return cartRepo.findByProduct(product);
		} else if (maxPrice != null) {
			return cartRepo.findByPriceLessThanEqual(maxPrice);
		} else if (prefix != null) {
			return cartRepo.findByProductStartingWith(prefix);
		} else if (pageSize != null) {
			List<CartItems> cartItems = cartRepo.findAll();
			if (cartItems.size() > pageSize) {
				for (int i = pageSize; i < cartItems.size(); i++) {
					cartItems.remove(i);
				}
			}
			return cartItems;
		} else {
			return cartRepo.findAll();
		}
	}
	@GetMapping("/cart-items/{id}")
	public CartItems getCartItemsById(@RequestParam String id) {
		return cartRepo.findById(id).orElseThrow(() -> new CartItemsNotFoundException(id));
	}
	@PostMapping("/cart-items")
	@ResponseStatus(HttpStatus.CREATED)
	public CartItems create(@RequestBody CartItems cartItems) {
		cartRepo.insert(cartItems);
		return cartItems;
	}
//	@PutMapping("/cart-items/{id}")
//	public CartItems editCartItem(@PathVariable("id") String id, @RequestBody CartItems cartItems) {
//		cartRepo.save(cartItems);
//		return cartItems;
//	}
	@DeleteMapping("/cart-items{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCartItem(@PathVariable("id") String id) {
		cartRepo.deleteById(id);
	}
	@GetMapping("/cart-items/total-cost")
	public Double getTotalCost() {
		List<CartItems> cartList = cartRepo.findAll();
		Double subtotal = 0.0;
		for (int i = 0; i < cartList.size(); i++) {
			subtotal += (cartList.get(i).getPrice() * cartList.get(i).getQuantity());
		}
		return (subtotal * 1.06);
	}
	@PatchMapping("/cart-items/{id}/add")
	public CartItems changeItemQuantity(@PathVariable("id") String id, @RequestParam Integer count) {
		Optional<CartItems> optCart = cartRepo.findById(id);
		if (optCart != null) {
			CartItems cartItemsToEdit = optCart.get();
			cartItemsToEdit.setQuantity(cartItemsToEdit.getQuantity() + count);
			return cartItemsToEdit;
		}
		return null;
	}
	@ResponseBody
	@ExceptionHandler(CartItemsNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String CartItemsNotFoundHandler(CartItemsNotFoundException ex) {
		return ex.getMessage();
	}
}
