import { RouterProvider } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { router } from "./router";
import { Toaster } from "sonner";   

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5,   // 5 min cache
      retry: 1,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      <Toaster position="bottom-right" theme="dark" richColors toastOptions={{
          style: {
            background: '#0F1623',
            border: '1px solid rgba(255,255,255,0.08)',
            color: '#fff',
          },
        }}/>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}