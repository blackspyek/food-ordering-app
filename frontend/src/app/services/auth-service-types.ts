export interface BackendAuthResponse {
	data: {
		token: string;
		expiresIn: number;
		roles: string[];
	};
	message: string;
	status: number;
}
export interface AuthState {
	token: string | null;
	roles: string[] | null;
	userId: number | null;
	name: string | null;
	email: string | null;
}
export interface Authority {
	authority: string;
}
export interface BackendSignUpResponse {
	data: RegisteredUser;
	message: string;
	status: number;
}
interface RegisteredUser {
	id: number;
	email: string;
	phoneNumber: string;
	name: string;
	enabled: boolean;
	roles: string[];
	username: string;
	authorities: Authority[];
	credentialsNonExpired: boolean;
	accountNonExpired: boolean;
	accountNonLocked: boolean;
}
export interface BackendSignUpResponse {
	data: RegisteredUser;
	message: string;
	status: number;
}

export default AuthState;
