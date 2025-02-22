/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import 'package:flutter/material.dart';

class BeamColors {
  static const transparent = Colors.transparent;
  static const white = Colors.white;
  static const black = Colors.black;
  static const grey1 = Color(0xffDFE1E3);
  static const grey2 = Color(0xffCBCBCB);
  static const grey3 = Color(0xffA0A4AB);
  static const grey4 = Color(0x30808080);
  static const darkGrey = Color(0xff2E2E34);
  static const darkBlue = Color(0xff242639);

  static const green = Color(0xff37AC66);
  static const orange = Color(0xffEEAB00);
  static const red = Color(0xffE54545);
}

class BeamGraphColors {
  static const node = BeamColors.grey3;
  static const border = Color(0xFF45454E);
  static const edge = BeamLightThemeColors.primary;
}

class BeamNotificationColors {
  static const error = Color(0xFFE54545);
  static const info = Color(0xFF3E67F6);
  static const success = Color(0xFF37AC66);
  static const warning = Color(0xFFEEAB00);
}

class BeamLightThemeColors {
  static const border = Color(0xFFE5E5E5);
  static const primaryBackground = BeamColors.white;
  static const secondaryBackground = Color(0xffFCFCFC);
  static const grey = Color(0xffE5E5E5);
  static const listBackground = Color(0xFFA0A4AB);
  static const text = BeamColors.darkBlue;
  static const primary = Color(0xffE74D1A);
  static const icon = Color(0xFFA0A4AB);

  static const code1 = Color(0xFFDA2833);
  static const code2 = Color(0xFF5929B4);
  static const codeComment = Color(0xFF4C6B60);
}

class BeamDarkThemeColors {
  static const border = Color(0xFFA0A4AB);
  static const primaryBackground = Color(0xff18181B);
  static const secondaryBackground = BeamColors.darkGrey;
  static const grey = Color(0xff3F3F46);
  static const listBackground = Color(0xFF606772);
  static const text = Color(0xffFFFFFF);
  static const primary = Color(0xffF26628);
  static const icon = Color(0xFF606772);

  static const code1 = Color(0xFFDA2833);
  static const code2 = Color(0xFF5929B4);
  static const codeComment = Color(0xFF4C6B60);
}
