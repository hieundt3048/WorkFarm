import { useEffect, useMemo, useState } from 'react';
import { Leaf, LogIn, UserPlus } from 'lucide-react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

const routes = {
  login: '/login',
  register: '/register',
  home: '/home',
};

const readPathname = () => {
  const path = window.location.pathname.toLowerCase();
  if (path === routes.home) {
    return routes.home;
  }
  return path === routes.register ? routes.register : routes.login;
};

const navigate = (path) => {
  window.history.pushState({}, '', path);
  window.dispatchEvent(new PopStateEvent('popstate'));
};

const NatureLayout = ({ title, subtitle, children, footer }) => (
  <div className="nature-bg min-h-screen px-4 py-8 sm:px-6 lg:px-8">
    <div className="mx-auto grid min-h-[88vh] max-w-6xl overflow-hidden rounded-[2rem] border border-emerald-200/70 bg-white/70 shadow-[0_30px_80px_-35px_rgba(16,185,129,0.55)] backdrop-blur md:grid-cols-[1.1fr_1fr]">
      <aside className="relative overflow-hidden bg-[linear-gradient(145deg,#166534_0%,#2f855a_45%,#65a30d_100%)] p-8 text-emerald-50 sm:p-10">
        <div className="absolute -left-10 -top-10 h-44 w-44 rounded-full bg-emerald-200/20 blur-2xl" />
        <div className="absolute bottom-2 right-2 h-56 w-56 rounded-full bg-lime-100/20 blur-2xl" />

        <div className="relative z-10 flex h-full flex-col justify-between">
          <div>
            <div className="mb-6 inline-flex items-center gap-2 rounded-full border border-emerald-100/35 bg-emerald-100/10 px-4 py-2 text-sm font-semibold tracking-wide">
              <Leaf size={16} />
              WorkFarm Green Space
            </div>
            <h1 className="text-4xl font-black leading-tight sm:text-5xl">Bình yên để tập trung</h1>
            <p className="mt-4 max-w-md text-sm text-emerald-50/90 sm:text-base">
              Đăng nhập hoặc tạo tài khoản mới để bắt đầu hành trình quản lý thời gian trong không gian xanh dịu mắt.
            </p>
          </div>

          <div className="mt-8 rounded-2xl border border-emerald-100/30 bg-emerald-50/10 p-5">
            <p className="text-xs uppercase tracking-[0.22em] text-emerald-100/85">Nature Rhythm</p>
            <p className="mt-2 text-lg font-semibold">Breath in. Focus out.</p>
            <p className="mt-2 text-sm text-emerald-50/85">Màu xanh giúp giữ trạng thái bình tĩnh và tăng độ bền chú ý theo từng phiên làm việc.</p>
          </div>
        </div>
      </aside>

      <section className="flex items-center bg-[radial-gradient(circle_at_20%_0%,rgba(132,204,22,0.12),transparent_40%),radial-gradient(circle_at_90%_80%,rgba(16,185,129,0.16),transparent_38%),#f8fff8] p-6 sm:p-10">
        <div className="w-full">
          <p className="mb-2 text-xs font-bold uppercase tracking-[0.24em] text-emerald-700">WorkFarm Account</p>
          <h2 className="text-3xl font-black text-emerald-950">{title}</h2>
          <p className="mt-2 text-sm text-emerald-800/85">{subtitle}</p>
          <div className="mt-8">{children}</div>
          <div className="mt-6 text-sm text-emerald-900/90">{footer}</div>
        </div>
      </section>
    </div>
  </div>
);

const LoginPage = () => {
  const [form, setForm] = useState({ email: '', password: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [feedback, setFeedback] = useState('');

  const onSubmit = async (event) => {
    event.preventDefault();
    setFeedback('');

    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      });

      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(data.message || 'Đăng nhập thất bại.');
      }

      localStorage.setItem('focus-user-id', data.userId || '');
      localStorage.setItem('focus-user-role', data.role || 'USER');
      setFeedback(`Đăng nhập thành công. Xin chào ${data.username || 'bạn'}!`);
      navigate(routes.home);
    } catch (error) {
      setFeedback(error.message || 'Có lỗi khi đăng nhập.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <NatureLayout
      title="Đăng nhập"
      subtitle="Tiếp tục phiên làm việc xanh của bạn"
      footer={
        <>
          Chưa có tài khoản?{' '}
          <button onClick={() => navigate(routes.register)} className="font-bold text-emerald-700 underline decoration-emerald-400 underline-offset-4">
            Tạo tài khoản mới
          </button>
        </>
      }
    >
      <form onSubmit={onSubmit} className="space-y-4">
        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Email</span>
          <input
            type="email"
            required
            value={form.email}
            onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
            placeholder="you@greenmail.com"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Mật khẩu</span>
          <input
            type="password"
            required
            value={form.password}
            onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))}
            placeholder="••••••••"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <button
          type="submit"
          disabled={isLoading}
          className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-[linear-gradient(120deg,#166534,#15803d)] px-4 py-3 font-bold text-emerald-50 shadow-lg shadow-emerald-900/20 transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-70"
        >
          <LogIn size={18} />
          {isLoading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>
      </form>

      {feedback && <p className="mt-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">{feedback}</p>}
    </NatureLayout>
  );
};

const HomePage = () => {
  const userId = localStorage.getItem('focus-user-id');
  const userRole = localStorage.getItem('focus-user-role') || 'USER';

  useEffect(() => {
    if (!userId) {
      navigate(routes.login);
    }
  }, [userId]);

  const onLogout = () => {
    localStorage.removeItem('focus-user-id');
    localStorage.removeItem('focus-user-role');
    navigate(routes.login);
  };

  return (
    <NatureLayout
      title="Trang chủ"
      subtitle="Bạn đã đăng nhập thành công vào WorkFarm"
      footer={
        <button onClick={onLogout} className="font-bold text-emerald-700 underline decoration-emerald-400 underline-offset-4">
          Đăng xuất
        </button>
      }
    >
      <div className="space-y-4 rounded-2xl border border-emerald-200 bg-white/70 p-5 text-emerald-900 shadow-sm">
        <p className="text-sm">
          <span className="font-semibold">User ID:</span> {userId}
        </p>
        <p className="text-sm">
          <span className="font-semibold">Vai trò:</span> {userRole}
        </p>
        <p className="text-sm text-emerald-800/90">Chào mừng bạn quay lại. Từ đây bạn có thể mở rộng thêm dashboard, timer và các chức năng quản lý công việc.</p>
      </div>
    </NatureLayout>
  );
};

const RegisterPage = () => {
  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [feedback, setFeedback] = useState('');

  const onSubmit = async (event) => {
    event.preventDefault();
    setFeedback('');

    if (form.password !== form.confirmPassword) {
      setFeedback('Mật khẩu nhập lại không khớp.');
      return;
    }

    const payload = {
      username: form.username,
      email: form.email,
      password: form.password,
    };

    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });

      const data = await response.json().catch(() => ({}));
      if (!response.ok) {
        throw new Error(data.message || 'Đăng ký thất bại.');
      }

      setFeedback('Đăng ký thành công. Bạn có thể đăng nhập ngay bây giờ.');
      setForm({ username: '', email: '', password: '', confirmPassword: '' });
    } catch (error) {
      setFeedback(error.message || 'Có lỗi khi đăng ký.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <NatureLayout
      title="Đăng ký"
      subtitle="Tạo tài khoản để bắt đầu quản lý thời gian"
      footer={
        <>
          Đã có tài khoản?{' '}
          <button onClick={() => navigate(routes.login)} className="font-bold text-emerald-700 underline decoration-emerald-400 underline-offset-4">
            Quay về đăng nhập
          </button>
        </>
      }
    >
      <form onSubmit={onSubmit} className="space-y-4">
        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Tên người dùng</span>
          <input
            type="text"
            required
            value={form.username}
            onChange={(e) => setForm((prev) => ({ ...prev, username: e.target.value }))}
            placeholder="focus.farmer"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Email</span>
          <input
            type="email"
            required
            value={form.email}
            onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
            placeholder="you@greenmail.com"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Mật khẩu</span>
          <input
            type="password"
            required
            value={form.password}
            onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))}
            placeholder="Tối thiểu 8 ký tự"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <label className="block">
          <span className="mb-1 block text-sm font-semibold text-emerald-900">Nhập lại mật khẩu</span>
          <input
            type="password"
            required
            value={form.confirmPassword}
            onChange={(e) => setForm((prev) => ({ ...prev, confirmPassword: e.target.value }))}
            placeholder="Nhập lại mật khẩu"
            className="w-full rounded-xl border border-emerald-300 bg-white px-4 py-3 text-sm text-emerald-900 outline-none ring-emerald-300 transition focus:ring"
          />
        </label>

        <button
          type="submit"
          disabled={isLoading}
          className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-[linear-gradient(120deg,#15803d,#4d7c0f)] px-4 py-3 font-bold text-emerald-50 shadow-lg shadow-emerald-900/20 transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-70"
        >
          <UserPlus size={18} />
          {isLoading ? 'Đang tạo tài khoản...' : 'Đăng ký'}
        </button>
      </form>

      {feedback && <p className="mt-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-900">{feedback}</p>}
    </NatureLayout>
  );
};

const App = () => {
  const [pathname, setPathname] = useState(readPathname);

  useEffect(() => {
    const handleRouteChange = () => setPathname(readPathname());
    window.addEventListener('popstate', handleRouteChange);
    return () => window.removeEventListener('popstate', handleRouteChange);
  }, []);

  const page = useMemo(() => {
    if (pathname === routes.home) {
      return <HomePage />;
    }
    if (pathname === routes.register) {
      return <RegisterPage />;
    }
    return <LoginPage />;
  }, [pathname]);

  return page;
};

export default App;
