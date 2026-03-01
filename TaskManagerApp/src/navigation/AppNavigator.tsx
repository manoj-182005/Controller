import React, { useState } from 'react';
import { View, TouchableOpacity, StyleSheet, Text } from 'react-native';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import DashboardScreen from '../screens/DashboardScreen';
import CalendarScreen from '../screens/CalendarScreen';
import AnalyticsScreen from '../screens/AnalyticsScreen';
import ViewsScreen from '../screens/ViewsScreen';
import ListViewScreen from '../screens/ListViewScreen';
import KanbanScreen from '../screens/KanbanScreen';
import TimeBlockScreen from '../screens/TimeBlockScreen';
import FocusScreen from '../screens/FocusScreen';
import { QuickAddModal } from '../components/QuickAddModal';
import { THEME } from '../theme/tokens';

const Tab = createBottomTabNavigator();
const ViewsStack = createNativeStackNavigator();

function ViewsNavigator() {
  return (
    <ViewsStack.Navigator
      screenOptions={{
        headerShown: false,
        contentStyle: { backgroundColor: THEME.colors.bg },
      }}
    >
      <ViewsStack.Screen name="ViewsHome" component={ViewsScreen} />
      <ViewsStack.Screen name="ListView" component={ListViewScreen} />
      <ViewsStack.Screen name="Kanban" component={KanbanScreen} />
      <ViewsStack.Screen name="TimeBlock" component={TimeBlockScreen} />
      <ViewsStack.Screen name="Focus" component={FocusScreen} />
    </ViewsStack.Navigator>
  );
}

const TAB_ICONS: Record<string, string> = {
  Home: '🏠',
  Calendar: '📅',
  Add: '＋',
  Views: '⊞',
  Analytics: '📊',
};

interface AddButtonProps {
  onPress: () => void;
}

function AddButton({ onPress }: AddButtonProps) {
  return (
    <TouchableOpacity style={styles.addButton} onPress={onPress} activeOpacity={0.8}>
      <Text style={styles.addButtonText}>＋</Text>
    </TouchableOpacity>
  );
}

export default function AppNavigator() {
  const [quickAddVisible, setQuickAddVisible] = useState(false);

  return (
    <NavigationContainer>
      <Tab.Navigator
        screenOptions={({ route }) => ({
          headerShown: false,
          tabBarStyle: styles.tabBar,
          tabBarActiveTintColor: THEME.colors.accent,
          tabBarInactiveTintColor: THEME.colors.text.muted,
          tabBarLabelStyle: styles.tabLabel,
          tabBarIcon: ({ focused, color }) => {
            const icon = TAB_ICONS[route.name] ?? '●';
            return (
              <Text style={[styles.tabIcon, { opacity: focused ? 1 : 0.5 }]}>{icon}</Text>
            );
          },
        })}
      >
        <Tab.Screen name="Home" component={DashboardScreen} />
        <Tab.Screen name="Calendar" component={CalendarScreen} />
        <Tab.Screen
          name="Add"
          component={DashboardScreen} // placeholder, intercepted below
          options={{
            tabBarButton: () => (
              <AddButton onPress={() => setQuickAddVisible(true)} />
            ),
          }}
        />
        <Tab.Screen name="Views" component={ViewsNavigator} />
        <Tab.Screen name="Analytics" component={AnalyticsScreen} />
      </Tab.Navigator>

      <QuickAddModal
        visible={quickAddVisible}
        onClose={() => setQuickAddVisible(false)}
      />
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  tabBar: {
    backgroundColor: '#0D1525',
    borderTopColor: THEME.colors.border,
    borderTopWidth: 1,
    height: 64,
    paddingBottom: 8,
    paddingTop: 8,
  },
  tabIcon: {
    fontSize: 22,
  },
  tabLabel: {
    fontSize: 10,
    fontWeight: '600',
  },
  addButton: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: THEME.colors.accent,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
    shadowColor: THEME.colors.accent,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.4,
    shadowRadius: 8,
    elevation: 8,
  },
  addButtonText: {
    fontSize: 28,
    color: '#fff',
    lineHeight: 32,
    fontWeight: '300',
  },
});
