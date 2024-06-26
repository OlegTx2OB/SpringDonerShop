package com.example.papadoner.service.impl;

import com.example.papadoner.dto.UserDto;
import com.example.papadoner.model.Order;
import com.example.papadoner.model.User;
import com.example.papadoner.repository.OrderRepository;
import com.example.papadoner.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mUserRepository;
    @Mock
    private OrderRepository mOrderRepository;
    @InjectMocks
    private UserServiceImpl mUserService;

    @Test
    void correctConstructorTest() {
        UserRepository userRepository = mock(UserRepository.class);
        OrderRepository orderRepository = mock(OrderRepository.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, orderRepository);

        assertEquals(userRepository, userService.getMUserRepository());
        assertEquals(orderRepository, userService.getMOrderRepository());
    }

    @Test
    void getUserById_UserFound_Success() {
        // Arrange
        long userId = 1L;
        User user = new User(userId, 123, List.of());
        when(mUserRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDto result = mUserService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserById_UserNotFound_ExceptionThrown() {
        // Arrange
        long nonExistentUserId = 999L;
        when(mUserRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> mUserService.getUserById(nonExistentUserId));
    }

    @Test
    void updateUser_UserFound_SuccessTest() {
        // Arrange
        long userId = 1L;
        User oldUser = new User(userId, 100, List.of());
        User newUser = new User(userId, 123, List.of());

        when(mUserRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(mUserRepository.save(newUser)).thenReturn(newUser);

        // Act
        UserDto result = mUserService.updateUser(userId, newUser, null);

        // Assert
        assertNotNull(result);
        assertEquals(newUser.getId(), result.getId());
        verify(mUserRepository, times(1)).save(newUser);
    }


    @Test
    void deleteUser_Success() {
        // Arrange
        long userId = 1L;

        // Act
        mUserService.deleteUser(userId);

        // Assert
        verify(mUserRepository, times(1)).deleteById(userId);
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<User> users = List.of(new User(), new User());
        when(mUserRepository.findAll()).thenReturn(users);

        // Act
        List<UserDto> result = mUserService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(users.size(), result.size());
    }

    @Test
    void findUsersWithMoreOrdersThan_Success() {
        // Arrange
        int count = 2;
        List<User> users = List.of(new User(), new User());
        when(mUserRepository.findUsersWithMoreOrdersThan(count)).thenReturn(users);

        // Act
        List<UserDto> result = mUserService.findUsersWithMoreOrdersThan(count);

        // Assert
        assertNotNull(result);
        assertEquals(users.size(), result.size());
    }

    @Test
    void setOrders_NullOrderIds_UserOrdersNotSet() {
        // Given
        User user = new User();
        Set<Long> orderIds = null;

        // When
        User result = mUserService.setOrders(user, orderIds);

        // Then
        assertEquals(List.of(),result.getOrders());
    }

    @Test
    void setOrders_EmptyOrderIds_UserOrdersNotSet() {
        // Given
        User user = new User();
        Set<Long> orderIds = Set.of();

        // When
        User result = mUserService.setOrders(user, orderIds);

        // Then
        assertEquals(List.of(), result.getOrders());
    }

    @Test
    void setOrders_OrderIdsWithExistingOrders_UserOrdersSet() {
        // Setup
        User user = new User();
        Set<Long> orderIds = new HashSet<>(Arrays.asList(1L, 2L));
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        when(mOrderRepository.findById(1L)).thenReturn(Optional.of(order1));
        when(mOrderRepository.findById(2L)).thenReturn(Optional.of(order2));

        // When
        User result = mUserService.setOrders(user, orderIds);

        // Then
        assertNotNull(result.getOrders());
        assertEquals(2, result.getOrders().size());
        assertTrue(result.getOrders().contains(order1));
        assertTrue(result.getOrders().contains(order2));
    }

    @Test
    void setOrders_OrderIdsWithNonExistingOrders_EntityNotFoundExceptionThrown() {
        // Given
        User user = new User();
        Set<Long> orderIds = new HashSet<>(Collections.singletonList(1L));
        when(mOrderRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(EntityNotFoundException.class, () -> mUserService.setOrders(user, orderIds));
    }
}
