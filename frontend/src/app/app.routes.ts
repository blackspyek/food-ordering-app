import { Routes } from '@angular/router';
import { MainLayout } from './pages/main-layout/main-layout';
import { DeliverySelection } from './pages/delivery-selection/delivery-selection';
import { MenuListing } from './pages/menu-listing/menu-listing';
import { deliveryMethodGuard } from './guards/delivery-method-guard';
import { Orders } from './pages/orders/orders';
import { Cart } from './pages/cart/cart';
import { emptyBasketGuard } from './guards/empty-basket-guard';
import { OrderFinalization } from './pages/order-finalization/order-finalization';
import { OrderDetails } from './pages/order-details/order-details';
import { NotFoundPage } from './pages/not-found/not-found';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { OrdersManagement } from './pages/orders-management/orders-management';
import { employeeAuthGuard } from './guards/employee-auth-guard';
import { ForgotPassword } from './pages/forgot-password/forgot-password';
import { ResetPassword } from './pages/reset-password/reset-password';
import { Reports } from './pages/reports/reports';
import { managerAuthGuard } from './guards/manager-auth-guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    children: [
      {
        path: '',
        component: DeliverySelection,
        data: { view: 'selecting' },
      },
      {
        path: 'menu',
        component: MenuListing,
        data: { view: 'showing-menu' },
        canActivate: [deliveryMethodGuard],
      },
      {
        path: 'orders',
        component: Orders,
        data: { view: 'orders' },
      },
      {
        path: 'cart',
        component: Cart,
        data: { view: 'cart' },
        canActivate: [emptyBasketGuard],
      },
      {
        path: 'finalize-order',
        component: OrderFinalization,
        data: { view: 'finalizing-order' },
        canActivate: [emptyBasketGuard],
      },
      {
        path: 'order-details/:orderId',
        data: { view: 'order-details' },
        component: OrderDetails,
      },
      {
        path: 'login',
        component: Login,
        data: { view: 'login' },
      },
      {
        path: 'register',
        component: Register,
        data: { view: 'register' },
      },
      {
        path: 'forgot-password',
        component: ForgotPassword,
        data: { view: 'forgot-password' },
      },
      {
        path: 'reset-password',
        component: ResetPassword,
        data: { view: 'reset-password' },
      },
      {
        path: 'orders-management',
        component: OrdersManagement,
        data: { view: 'orders-management' },
        canActivate: [employeeAuthGuard],
      },
      {
        path: 'reports',
        component: Reports,
        data: { view: 'reports' },
        canActivate: [managerAuthGuard],
      },
      {
        path: '**',
        component: NotFoundPage,
      },
    ],
  },
];
