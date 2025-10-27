import { Component } from "@angular/core";
import { RouterLink } from "@angular/router";

@Component({
	selector: "app-not-found",
	standalone: true,
	imports: [RouterLink],
	template: `
    <div class="flex flex-col items-center justify-center h-full w-full p-6 text-center font-[Roboto]">

      <div class="relative mb-6">
        <div class="absolute inset-0 bg-orange-100 rounded-full blur-xl opacity-50"></div>
        <div class="relative bg-orange-50 p-8 rounded-full shadow-sm">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="w-20 h-20 text-[#FDA403]">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126ZM12 15.75h.007v.008H12v-.008Z" />
          </svg>
        </div>
      </div>

      <h1 class="text-8xl font-black text-gray-200 tracking-tighter leading-none select-none">
        404
      </h1>

      <h2 class="text-2xl md:text-3xl font-bold text-gray-800 mt-2 mb-4">
        Oops! Empty plate.
      </h2>

      <p class="text-gray-500 text-lg max-w-md mb-10 leading-relaxed">
        It looks like the page you're looking for was already eaten or never existed.
      </p>

      <a
        routerLink="/menu"
        class="bg-[#FDA403] hover:bg-orange-500 text-white font-bold py-3 px-10 rounded-full shadow-lg shadow-orange-200 transition-all transform hover:scale-105 active:scale-95 text-lg no-underline inline-flex items-center gap-2"
      >
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="w-5 h-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 19.5 3 12m0 0 7.5-7.5M3 12h18" />
        </svg>
        Back to Menu
      </a>

    </div>
  `,
	styles: [
		`
    :host {
      display: block;
      height: 100%;
      width: 100%;
    }
  `,
	],
})
export class NotFoundPage {}
