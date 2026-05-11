import routes from './routes';

const COMMERCIAL_PATH_SEGMENT = '/commercial/';
const COMMERCIAL_COMPONENT_SEGMENT = './commercial/';

function pruneCommercial(routeList) {
  if (!Array.isArray(routeList)) return [];

  return routeList
    .map((route) => {
      const next = { ...route };

      if (Array.isArray(route.routes)) {
        next.routes = pruneCommercial(route.routes);
      }

      return next;
    })
    .filter((route) => {
      const path = String(route.path || '');
      const component = String(route.component || '');
      const hasCommercialRef =
        path.includes(COMMERCIAL_PATH_SEGMENT) || component.includes(COMMERCIAL_COMPONENT_SEGMENT);

      if (hasCommercialRef) return false;

      // Keep leaf routes and redirects as-is.
      if (!Array.isArray(route.routes)) return true;

      // Keep nodes that still have children after pruning.
      if (route.routes.length > 0) return true;

      // Remove empty route groups created after pruning.
      return !!route.component || !!route.redirect;
    });
}

export default pruneCommercial(routes);
